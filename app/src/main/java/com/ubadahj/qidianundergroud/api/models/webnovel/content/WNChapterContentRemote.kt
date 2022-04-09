package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNChapterContentRemote(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: WNDataRemote,
    @Json(name = "msg")
    val msg: String
)