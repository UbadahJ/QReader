package com.ubadahj.qidianundergroud.repositories

import android.content.Context
import android.webkit.WebView
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.ubadahj.qidianundergroud.Database
import com.ubadahj.qidianundergroud.api.WebNovelApi
import com.ubadahj.qidianundergroud.models.Chapter
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.utils.getHtml
import com.ubadahj.qidianundergroud.utils.md5
import com.ubadahj.qidianundergroud.utils.unescapeHtml
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withTimeoutOrNull
import org.jsoup.Jsoup
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val database: Database,
    private val webNovelApi: WebNovelApi
) {

    companion object {
        private const val maxTimeDelay: Long = 8000
    }

    suspend fun getChaptersContent(
        webViewFactory: (Context) -> WebView,
        group: ChapterGroup,
        refresh: Boolean = false
    ): Flow<List<Chapter>> {
        val dbChapters = database.chapterGroupQueries.contents(group.link).executeAsList()
        if (refresh || dbChapters.isEmpty()) {
            when {
                "vim" in group.link -> fetchDefaultChapters(webViewFactory, group)
                "pstbn" in group.link -> fetchDefaultChapters(webViewFactory, group)
                else -> fetchWebNovelChapters(group)
            }
        }

        return database.chapterGroupQueries.contents(group.link).asFlow().mapToList()
    }

    suspend fun fetchWebNovelChapters(group: ChapterGroup) {
        database.chapterQueries.insert(webNovelApi.getChapterContents(group))
    }

    suspend fun fetchDefaultChapters(
        webViewFactory: (Context) -> WebView,
        group: ChapterGroup,
    ) {
        val webView = webViewFactory(context).apply {
            loadUrl(group.link)
        }
        var doc = Jsoup.parse(webView.getHtml())
        withTimeoutOrNull(maxTimeDelay) {
            while ("Chapter" !in doc.text()) {
                doc = Jsoup.parse(webView.getHtml())
                delay(300)
            }
            true
        } ?: throw TimeoutException("Exceed $maxTimeDelay fetching contents")

        doc.select("br").forEach { it.remove() }
        database.chapterQueries.transaction {
            doc.select(".well")
                .filter { "Chapter" in it.text() }
                .filter { it.select("h2.text-center").first() != null }
                .forEach {
                    val title = it.select("h2.text-center").first()?.html()?.unescapeHtml()
                    val contents = it.select("p").outerHtml().unescapeHtml()
                    database.chapterQueries.insertByValues(
                        group.link.md5 + title?.md5,
                        group.link,
                        title ?: "",
                        contents
                    )
                }
        }
        webView.clearCache(true)
    }

}
