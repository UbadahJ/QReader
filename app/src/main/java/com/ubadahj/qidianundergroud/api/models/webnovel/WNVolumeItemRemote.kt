package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNVolumeItemRemote(
    @Json(name = "chapterCount")
    val chapterCount: Int,
    @Json(name = "chapterItems")
    val chapterItems: List<WNChapterItemRemote>,
    @Json(name = "index")
    val index: Int,
    @Json(name = "name")
    val name: String
)
