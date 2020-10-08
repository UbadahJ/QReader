package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.api.models.BookJson
import com.ubadahj.qidianundergroud.api.models.ChapterGroupJson
import com.ubadahj.qidianundergroud.database.moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Api(private val proxy: Boolean = false) {

    companion object {
        private val client: OkHttpClient = OkHttpClient.Builder().apply {
            networkInterceptors().add(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            })
            cookieJar(MemoryCookieJar())
        }.build()

        private val api: UndergroundApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://toc.qidianunderground.org/api/v1/pages/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
                .create(UndergroundApi::class.java)
        }

        private val proxyApi: ProxyApi by lazy {
            Retrofit.Builder()
                .baseUrl("http://us7.unblock-websites.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
                .create(ProxyApi::class.java)
        }
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
                "https://toc.qidianunderground.org/api/v1/pages/public/${bookId}/chapters",
                "rnd"
            )
        return api.getChapters(bookId)
    }
}