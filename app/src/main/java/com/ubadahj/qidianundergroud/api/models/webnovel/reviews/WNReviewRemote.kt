package com.ubadahj.qidianundergroud.api.models.webnovel.reviews


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewRemote(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: WNReviewData,
    @Json(name = "msg")
    val msg: String
)