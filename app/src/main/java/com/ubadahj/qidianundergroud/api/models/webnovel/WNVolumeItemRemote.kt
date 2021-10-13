package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNVolumeItemRemote(
    @Json(name = "chapterItems")
    val chapterItems: List<WNChapterItemRemote>,
    @Json(name = "volumeId")
    val id: Int,
    @Json(name = "volumeName")
    val name: String
)
