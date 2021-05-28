package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.api.models.undeground.BookJson
import com.ubadahj.qidianundergroud.api.models.undeground.ChapterGroupJson
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProxyApi {

    @FormUrlEncoded
    @POST("/")
    suspend fun getBooks(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<BookJson>

    @FormUrlEncoded
    @POST("/")
    suspend fun getChapters(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<ChapterGroupJson>

}
