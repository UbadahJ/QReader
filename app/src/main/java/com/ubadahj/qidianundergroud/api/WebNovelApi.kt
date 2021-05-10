package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.api.models.webnovel.WNBookRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.WNRawChapterLinksRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.WNSearchResultRemote
import com.ubadahj.qidianundergroud.models.Book
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object WebNovelApi {

    private const val WEB_NOVEL_URL = "https://www.webnovel.com/"

    private val webNovelApi: IWebNovelApi by lazy {
        Api.getRetrofit(WEB_NOVEL_URL)
            .create(IWebNovelApi::class.java)
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

    private fun getToken(): String {
        return Api.client.cookieJar.loadForRequest(WEB_NOVEL_URL.toHttpUrl()).filter {
            it.name == "_csrfToken"
        }.first().value
    }

    private fun ResponseBody.parseSearchPage(): List<WNSearchResultRemote> {
        val results: MutableList<WNSearchResultRemote> = mutableListOf()
        val document = Jsoup.parse(string())
        val container = document.getElementsByClass("search-result-container").first()

        container.getElementsByTag("li").forEach { li ->
            val hyperlink = li.getElementsByTag("a").first()
            results.add(
                WNSearchResultRemote(
                    name = hyperlink.attr("title"),
                    link = "https:${hyperlink.attr("href")}",
                    tags = parseTags(li),
                    rating = parseRating(li),
                    desc = parseDescription(li)
                )
            )
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

    @GET("apiajax/chapter/GetChapterList")
    suspend fun getChaptersLinks(
        @Query("_csrfToken") csrfToken: String,
        @Query("bookId") bookId: String
    ): Response<WNRawChapterLinksRemote>

}