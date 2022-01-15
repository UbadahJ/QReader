package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.api.models.webnovel.WNBookRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.WNChapterRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.WNSearchResultRemote
import com.ubadahj.qidianundergroud.api.retrofit.IWebNovelApi
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Metadata
import com.ubadahj.qidianundergroud.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import javax.inject.Inject

private const val WEB_NOVEL_URL = "https://www.webnovel.com/"

class WebNovelApi @Inject constructor(
    private val client: OkHttpClient,
    private val provider: RetrofitProvider
) {

    private val webNovelApi: IWebNovelApi by lazy {
        provider.get(WEB_NOVEL_URL).create(IWebNovelApi::class.java)
    }

    suspend fun getBook(book: Book): WNBookRemote? {
        val searchPage = webNovelApi.searchBooks(book.name)
        val searchResult = searchPage.body()
            ?.parseSearchPage()
            ?.firstOrNull { it.name.equals(book.name, true) }
            ?: return null

        val bookPage = webNovelApi.getBook(searchResult.name, searchResult.id)
        return bookPage.body()?.parseBookPage(searchResult)
    }

    suspend fun getChapter(bookMeta: Metadata): List<WNChapterRemote>? {
        return webNovelApi.getChaptersLinks(getToken(), bookMeta.id).body()
            ?.data
            ?.volumeItems
            ?.flatMap { it.chapterItems }
            ?.map {
                WNChapterRemote(
                    it.name,
                    "${bookMeta.link}/${it.name}_${it.id}".replace("?", ""),
                    it.index,
                    it.isVip != 0
                )
            }
    }

    suspend fun getChapterContents(group: Group): Content {
        val html = withContext(Dispatchers.IO) {
            webNovelApi.getChapterContents(group.link).body()?.string()
                ?: throw IllegalStateException("Unable to fetch page")
        }

        val doc = Jsoup.parse(html)
        doc.getElementsByClass("pirate").forEach { it.remove() }

        val title = doc.select("h3.dib").text()
        val contents = doc.getElementsByClass("cha-content").first()
            ?.getElementsByTag("p")
            ?.joinToString("\n\n") { it.text().trim() }
            ?: throw IllegalStateException("Either chapter is premium or parsing failed")

        return Content(group.link.md5 + title.md5, group.link, title, contents)
    }

    private suspend fun getToken(): String {
        return try {
            client.cookieJar
                .loadForRequest(WEB_NOVEL_URL.toHttpUrl())
                .first { it.name == "_csrfToken" }
                .value
        } catch (e: NoSuchElementException) {
            webNovelApi.ping()
            getToken()
        }
    }

    private fun ResponseBody.parseSearchPage(): List<WNSearchResultRemote> {
        val results: MutableList<WNSearchResultRemote> = mutableListOf()
        val document = Jsoup.parse(string())
        val container = document.getElementsByClass("search-result-container").first()

        container?.getElementsByTag("li")?.forEach { li ->
            li.getElementsByTag("a").first()?.let { hyperlink ->
                results.add(
                    WNSearchResultRemote(
                        name = hyperlink.attr("title"),
                        link = hyperlink.attr("href"),
                        tags = parseTags(li),
                        rating = parseRating(li),
                        desc = parseDescription(li)
                    )
                )
            }
        }

        return results
    }

    private fun parseTags(element: Element): List<String> =
        try {
            element.selectFirst(".g_tags")
                ?.getElementsByTag("a")
                ?.map { it.text() }
                ?: listOf()
        } catch (e: NullPointerException) {
            listOf()
        }

    private fun parseRating(element: Element): Float =
        try {
            element.selectFirst(".g_star_num > small")
                ?.text()
                ?.toFloat()
                ?: 0f
        } catch (e: NullPointerException) {
            0f
        }

    private fun parseDescription(element: Element): String =
        try {
            element.selectFirst("p.fs16.c_000")
                ?.text()
                ?: ""
        } catch (e: NullPointerException) {
            ""
        }

    private fun ResponseBody.parseBookPage(searchResult: WNSearchResultRemote): WNBookRemote {
        val document = Jsoup.parse(string())
        val infoElement = document.getElementsByClass("det-info").first()

        val author = infoElement?.selectFirst("address > p")
            ?.children()
            ?.first { it.text().contains("author", true) }
            ?.nextElementSibling()
            ?.text()
            ?: ""

        val coverLink = infoElement?.getElementsByTag("img")
            ?.last { searchResult.name in it.attr("alt") }
            ?.attr("src")
            ?.let { "https:$it" }
            ?: ""

        val category = infoElement?.selectFirst(".det-hd-detail > a")
            ?.text()
            ?: ""

        val description = document.selectFirst(".j_synopsis > p")
            ?.html()
            ?.replace("\n", "")
            ?.replace("<br>", "\n")
            ?: ""

        return WNBookRemote(
            id = searchResult.id,
            link = searchResult.link,
            name = searchResult.name,
            rating = searchResult.rating,
            author = author,
            coverLink = coverLink,
            category = category,
            description = description
        )
    }
}

