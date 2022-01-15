package com.ubadahj.qidianundergroud.api.retrofit

import com.ubadahj.qidianundergroud.api.models.underground.UndergroundBook
import com.ubadahj.qidianundergroud.api.models.underground.UndergroundGroup
import retrofit2.http.GET
import retrofit2.http.Path

interface UndergroundApi {

    @GET("public/")
    suspend fun getBooks(): List<UndergroundBook>

    @GET("public/{id}/chapters/")
    suspend fun getChapters(@Path("id") id: String): List<UndergroundGroup>

}
