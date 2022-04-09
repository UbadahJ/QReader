package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNDataRemote(
    @Json(name = "chapterInfo")
    val chapterInfo: WNChapterInfoRemote
)