package com.ubadahj.qidianundergroud.api

import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import retrofit2.http.GET
import retrofit2.http.Path

interface UndergroundApi {

    @GET("public/")
    suspend fun _getBooks(): List<Book>

    @GET("public/{id}/chapters/")
    suspend fun _getChapters(@Path("id") id: String): List<ChapterGroup>

}