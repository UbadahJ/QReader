package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNBannerRemote(
    @Json(name = "coverUrl")
    val coverUrl: String,
    @Json(name = "desc")
    val desc: String
)