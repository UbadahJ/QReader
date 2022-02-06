package com.ubadahj.qidianundergroud.api.models.webnovel.reviews.replies


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewReplyRemote(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: WNReviewReplyData,
    @Json(name = "msg")
    val msg: String
)