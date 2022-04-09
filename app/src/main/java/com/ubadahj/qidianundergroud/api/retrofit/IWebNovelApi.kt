package com.ubadahj.qidianundergroud.api.retrofit

import com.ubadahj.qidianundergroud.api.models.webnovel.WNRawChapterLinksRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.content.WNChapterContentRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.reviews.WNReviewRemote
import com.ubadahj.qidianundergroud.api.models.webnovel.reviews.replies.WNReviewReplyRemote
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("go/pcm/bookReview/get-reviews")
    suspend fun getBookReviews(
        @Query("_csrfToken") csrfToken: String,
        @Query("bookId") bookId: String,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 9999,
        @Query("orderBy") orderBy: Int = 1,
        @Query("novelType") novelType: Int = 1,
        @Query("needSummary") needSummary: Int = 1
    ): Response<WNReviewRemote>

    @GET("go/pcm/bookReview/detail")
    suspend fun getBookReviewReplies(
        @Query("_csrfToken") csrfToken: String,
        @Query("bookId") bookId: String,
        @Query("reviewId") reviewId: Int,
        @Query("pageIndex") pageIndex: Int = 1,
        @Query("pageSize") pageSize: Int = 9999,
    ): Response<WNReviewReplyRemote>

    @GET("go/pcm/chapter/getContent")
    suspend fun getChapterContents(
        @Query("_csrfToken") csrfToken: String,
        @Query("bookId") bookId: String,
        @Query("chapterId") chapterId: String,
        @Query("encryptType") encryptType: Int = 3,
        @Query("font") font: String = "Merriweather"
    ): Response<WNChapterContentRemote>

}