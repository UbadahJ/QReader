package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNChapterItemRemote(
    @Json(name = "chapterLevel")
    val chapterLevel: Int,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "index")
    val index: Int,
    @Json(name = "isAuth")
    val isAuth: Int,
    @Json(name = "isVip")
    val isVip: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "userLevel")
    val userLevel: Int
)
