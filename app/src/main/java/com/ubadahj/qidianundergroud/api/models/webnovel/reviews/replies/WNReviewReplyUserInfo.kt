package com.ubadahj.qidianundergroud.api.models.webnovel.reviews.replies


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewReplyUserInfo(
    @Json(name = "isAuthor")
    val isAuthor: Int,
    @Json(name = "isViceModerator")
    val isViceModerator: Int,
    @Json(name = "userRole")
    val userRole: Int
)