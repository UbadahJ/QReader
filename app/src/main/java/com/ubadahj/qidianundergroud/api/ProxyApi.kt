package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProxyApi {

    @FormUrlEncoded
    @POST("/")
    suspend fun getBooks(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<Book>

    @FormUrlEncoded
    @POST("/")
    suspend fun getChapters(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<ChapterGroup>

}