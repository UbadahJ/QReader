package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNRawChapterLinksRemote(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: WNDataRemote,
    @Json(name = "msg")
    val msg: String
)
