package com.ubadahj.qidianundergroud.api.models.webnovel.reviews.replies


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewReplyPrivilegeInfo(
    @Json(name = "img")
    val img: String,
    @Json(name = "isPrivilege")
    val isPrivilege: Int
)