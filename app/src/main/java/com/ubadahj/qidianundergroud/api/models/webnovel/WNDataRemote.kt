package com.ubadahj.qidianundergroud.api.models.webnovel

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNDataRemote(
    @Json(name = "bookInfo")
    val bookInfo: WNBookInfoRemote,
    @Json(name = "volumeItems")
    val volumeItems: List<WNVolumeItemRemote>
)
