package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.api.models.undeground.BookJson
import com.ubadahj.qidianundergroud.api.models.undeground.ChapterGroupJson
import javax.inject.Inject

class Api @Inject constructor(
    private val provider: RetrofitProvider
) {

    private val proxy: Boolean = true

    private val api: UndergroundApi by lazy {
        provider.get("https://toc.qidianunderground.org/api/v1/pages/")
            .create(UndergroundApi::class.java)
    }

    private val proxyApi: ProxyApi by lazy {
        provider.get("http://us7.unblock-websites.com/")
            .create(ProxyApi::class.java)
    }

    suspend fun getBooks(): List<BookJson> {
        if (proxy)
            return proxyApi.getBooks(
                "https://toc.qidianunderground.org/api/v1/pages/public/",
                "rnd"
            )
        return api.getBooks()
    }

    suspend fun getChapters(bookId: String): List<ChapterGroupJson> {
        if (proxy)
            return proxyApi.getChapters(
                "https://toc.qidianunderground.org/api/v1/pages/public/$bookId/chapters",
                "rnd"
            )
        return api.getChapters(bookId)
    }
}
