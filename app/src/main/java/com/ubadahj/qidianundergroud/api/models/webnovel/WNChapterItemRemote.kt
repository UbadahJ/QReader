package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNChapterItemRemote(
    @Json(name = "chapterLevel")
    val chapterLevel: Int,
    @Json(name = "chapterId")
    val id: String,
    @Json(name = "chapterIndex")
    val index: Int,
    @Json(name = "isAuth")
    val isAuth: Int,
    @Json(name = "isVip")
    val isVip: Int,
    @Json(name = "chapterName")
    val name: String,
    @Json(name = "userLevel")
    val userLevel: Int
)
