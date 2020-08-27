package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface Api {

    companion object {

        private val client: OkHttpClient = OkHttpClient.Builder().apply {
            networkInterceptors().add(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            })
            cookieJar(MemoryCookieJar())
        }.build()

        private val api: Api by lazy {
            Retrofit.Builder()
                .baseUrl("https://toc.qidianunderground.org/api/v1/pages/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
                .create(Api::class.java)
        }

        private val proxyApi: Api by lazy {
            Retrofit.Builder()
                .baseUrl("http://us7.unblock-websites.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
                .create(Api::class.java)
        }

        suspend fun getBooks(proxy: Boolean = false): List<Book> {
            if (proxy)
                return proxyApi._proxyGetBooks(
                    "https://toc.qidianunderground.org/api/v1/pages/public/",
                    "rnd"
                )
            return api._getBooks()
        }

        suspend fun getChapters(book: Book, proxy: Boolean = false): List<ChapterGroup> {
            if (proxy)
                return proxyApi._proxyGetChapters(
                    "https://toc.qidianunderground.org/api/v1/pages/public/${book.id}/chapters",
                    "rnd"
                )
            return api._getChapters(book.id)
        }

    }

    @GET("public/")
    suspend fun _getBooks(): List<Book>

    @GET("public/{id}/chapters/")
    suspend fun _getChapters(@Path("id") id: String): List<ChapterGroup>

    @FormUrlEncoded
    @POST("/")
    suspend fun _proxyGetBooks(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<Book>

    @FormUrlEncoded
    @POST("/")
    suspend fun _proxyGetChapters(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<ChapterGroup>

}