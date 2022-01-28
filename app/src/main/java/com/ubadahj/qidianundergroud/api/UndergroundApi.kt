package com.ubadahj.qidianundergroud.api

import android.content.Context
import android.webkit.WebView
import com.ubadahj.qidianundergroud.api.models.underground.UndergroundBook
import com.ubadahj.qidianundergroud.api.models.underground.UndergroundGroup
import com.ubadahj.qidianundergroud.api.retrofit.ProxyApi
import com.ubadahj.qidianundergroud.api.retrofit.UndergroundApi
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.preferences.NetworkPreferences
import com.ubadahj.qidianundergroud.utils.getHtml
import com.ubadahj.qidianundergroud.utils.md5
import com.ubadahj.qidianundergroud.utils.unescapeHtml
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import org.jsoup.Jsoup
import java.util.concurrent.TimeoutException
import javax.inject.Inject

class UndergroundApi @Inject constructor(
    @ApplicationContext private val context: Context,
    private val provider: RetrofitProvider,
    private val pref: NetworkPreferences
) {

    private val proxy get() = pref.useProxy.get()
    private val maxTimeDelay get() = pref.requestTimeout.get()
    private val undergroundApi: UndergroundApi by lazy {
        provider.get("https://toc.qidianunderground.org/api/v1/pages/")
            .create(UndergroundApi::class.java)
    }

    private val proxyApi: ProxyApi by lazy {
        provider.get("http://us7.unblock-websites.com/")
            .create(ProxyApi::class.java)
    }

    suspend fun getBooks(): List<UndergroundBook> {
        if (proxy)
            return proxyApi.getBooks(
                "https://toc.qidianunderground.org/api/v1/pages/public/",
                "rnd"
            )
        return undergroundApi.getBooks()
    }

    suspend fun getChapters(bookId: String): List<UndergroundGroup> {
        if (proxy)
            return proxyApi.getChapters(
                "https://toc.qidianunderground.org/api/v1/pages/public/$bookId/chapters",
                "rnd"
            )
        return undergroundApi.getChapters(bookId)
    }

    suspend fun getContents(
        webViewFactory: (Context) -> WebView,
        group: Group,
    ): List<Content> {
        val webView = webViewFactory(context).apply { loadUrl(group.link) }
        var doc = Jsoup.parse(webView.getHtml())
        withTimeoutOrNull(maxTimeDelay) {
            while ("Chapter" !in doc.text()) {
                doc = Jsoup.parse(webView.getHtml())
                delay(300)
            }
            true
        } ?: throw TimeoutException("Exceed $maxTimeDelay fetching contents")

        doc.select("br").forEach { it.remove() }
        return doc.select(".well")
            .filter { "Chapter" in it.text() }
            .filter { it.select("h2.text-center").first() != null }
            .map {
                val title = it.select("h2.text-center").first()?.html()?.unescapeHtml() ?: ""
                val contents = it.select("p").outerHtml().unescapeHtml()
                Content(group.link.md5 + title.md5, group.link, title, contents)
            }.also {
                if (pref.disableWebViewCache.get()) webView.clearCache(true)
            }
    }
}
