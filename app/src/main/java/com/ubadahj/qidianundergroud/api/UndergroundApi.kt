package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.database.moshi
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface UndergroundApi {

    companion object {

        private val client: OkHttpClient = OkHttpClient.Builder().apply {
            networkInterceptors().add(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BASIC)
            })
            cookieJar(MemoryCookieJar())
        }.build()

        private val undergroundApi: UndergroundApi by lazy {
            Retrofit.Builder()
                .baseUrl("https://toc.qidianunderground.org/api/v1/pages/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
                .create(UndergroundApi::class.java)
        }

        private val proxyUndergroundApi: UndergroundApi by lazy {
            Retrofit.Builder()
                .baseUrl("http://us7.unblock-websites.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
                .create(UndergroundApi::class.java)
        }

        suspend fun getBooks(proxy: Boolean = false): List<Book> {
            if (proxy)
                return proxyUndergroundApi._proxyGetBooks(
                    "https://toc.qidianunderground.org/api/v1/pages/public/",
                    "rnd"
                )
            return undergroundApi._getBooks()
        }

        suspend fun getChapters(book: Book, proxy: Boolean = false): List<ChapterGroup> {
            if (proxy)
                return proxyUndergroundApi._proxyGetChapters(
                    "https://toc.qidianunderground.org/api/v1/pages/public/${book.id}/chapters",
                    "rnd"
                )
            return undergroundApi._getChapters(book.id)
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