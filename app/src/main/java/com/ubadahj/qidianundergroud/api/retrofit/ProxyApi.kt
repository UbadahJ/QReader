package com.ubadahj.qidianundergroud.api.retrofit

import com.ubadahj.qidianundergroud.api.models.underground.UndergroundBook
import com.ubadahj.qidianundergroud.api.models.underground.UndergroundGroup
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProxyApi {

    @FormUrlEncoded
    @POST("/")
    suspend fun getBooks(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<UndergroundBook>

    @FormUrlEncoded
    @POST("/")
    suspend fun getChapters(
        @Field("url") url: String,
        @Field("server") server: String = "rnd"
    ): List<UndergroundGroup>

}
