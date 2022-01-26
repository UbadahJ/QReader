package com.ubadahj.qidianundergroud.api.retrofit

import com.ubadahj.qidianundergroud.api.models.webnovel.WNRawChapterLinksRemote
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface IWebNovelApi {

    @GET("/")
    suspend fun ping(): Response<ResponseBody>

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