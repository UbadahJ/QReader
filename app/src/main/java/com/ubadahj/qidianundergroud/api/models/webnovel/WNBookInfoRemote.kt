package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNBookInfoRemote(
    @Json(name = "bookId")
    val bookId: String,
    @Json(name = "bookName")
    val bookName: String,
    @Json(name = "bookSubName")
    val bookSubName: String,
    @Json(name = "hasPrivilege")
    val hasPrivilege: Int,
    @Json(name = "newChapterId")
    val newChapterId: String,
    @Json(name = "newChapterIndex")
    val newChapterIndex: Int,
    @Json(name = "newChapterName")
    val newChapterName: String,
    @Json(name = "newChapterTime")
    val newChapterTime: String,
    @Json(name = "totalChapterNum")
    val totalChapterNum: Int
)
