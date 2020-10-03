package com.ubadahj.qidianundergroud.repositories

import android.webkit.WebView
import androidx.lifecycle.liveData
import com.github.ajalt.timberkt.d
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterContentItem
import com.ubadahj.qidianundergroud.utils.getHtml
import com.ubadahj.qidianundergroud.utils.unescapeHtml
import kotlinx.coroutines.delay
import org.jsoup.Jsoup
import java.util.concurrent.TimeoutException

class ChapterRepository {

    companion object {
        private const val maxTimeDelay: Int = 8000
        private val chapterContents: MutableMap<Pair<Book, ChapterGroup>, List<ChapterContentItem>> =
            mutableMapOf()
    }

    fun getChaptersContent(
        webView: WebView,
        book: Book,
        chapters: ChapterGroup,
        refresh: Boolean = false
    ) = liveData {
        emit(Resource.Loading())
        try {
            val key = Pair(book, chapters)
            val time = DelayCounter(maxTimeDelay)
            if (refresh || key !in chapterContents) {
                d { "getChaptersContent: $refresh || ${key !in chapterContents}" }
                var doc = Jsoup.parse(webView.getHtml())!!
                while ("Chapter" !in doc.text()) {
                    doc = Jsoup.parse(webView.getHtml())!!
                    delay(300)
                    time.update()
                }

                doc.select("br").forEach { it.remove() }
                val data = doc.select(".well")
                    .filter { "Chapter" in it.text() }
                    .filter { it.select("h2.text-center").first() != null }
                    .map {
                        ChapterContentItem(
                            it.select("h2.text-center").first().html().unescapeHtml(),
                            it.select("p").outerHtml().unescapeHtml()
                        )
                    }

                chapterContents[key] = data
            }

            emit(Resource.Success(chapterContents[key]))
        } catch (e: TimeoutException) {
            emit(Resource.Error<List<ChapterContentItem>>(e))
        }
    }

    private class DelayCounter(private val maxTime: Int) {

        private var total: Int = 0

        fun update() {
            total += 300
            if (total > maxTime)
                throw TimeoutException("Max time exceeded for loading")
        }

    }
}