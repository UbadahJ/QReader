package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.api.models.webnovel.WNBookRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.WNChapterRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.WNRawChapterLinksRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.WNSearchResultRemote
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Chapter
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Metadata
import com.ubadahj.qidianundergroud.utils.md5
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
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

    suspend fun getChapterContents(group: ChapterGroup): Chapter {
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

        return Chapter(
            group.link.md5 + title.md5,
            group.link,
            title,
            contents
        )
    }

    private fun getToken(): String {
        return client.cookieJar
            .loadForRequest(WEB_NOVEL_URL.toHttpUrl())
            .first { it.name == "_csrfToken" }
            .value
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

private interface IWebNovelApi {

    @GET("search")
    suspend fun searchBooks(@Query("keywords") query: String): Response<ResponseBody>

    @GET("book/{bookName}_{bookId}")
    suspend fun getBook(
        @Path("bookName") bookName: String,
        @Path("bookId") bookId: String
    ): Response<ResponseBody>

    @GET("go/pcm/chapter/get-chapter-list")
    suspend fun getChaptersLinks(
        @Query("_csrfToken") csrfToken: String,
        @Query("bookId") bookId: String
    ): Response<WNRawChapterLinksRemote>

    @GET
    suspend fun getChapterContents(
        @Url link: String
    ): Response<ResponseBody>

}
