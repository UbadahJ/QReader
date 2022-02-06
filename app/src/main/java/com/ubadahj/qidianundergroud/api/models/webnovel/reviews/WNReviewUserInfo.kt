package com.ubadahj.qidianundergroud.api.models.webnovel.reviews


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewUserInfo(
    @Json(name = "isViceModerator")
    val isViceModerator: Int,
    @Json(name = "userRole")
    val userRole: Int
)