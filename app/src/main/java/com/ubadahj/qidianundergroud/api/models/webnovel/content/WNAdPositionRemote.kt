package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNAdPositionRemote(
    @Json(name = "readChapterEnd")
    val readChapterEnd: Boolean
)