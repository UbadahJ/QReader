package com.ubadahj.qidianundergroud.api.models.webnovel.reviews.replies


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewReplyData(
    @Json(name = "baseInfo")
    val baseInfo: WNReviewReplyBaseInfo,
    @Json(name = "isLast")
    val isLast: Int,
    @Json(name = "replyItems")
    val replyItems: List<WNReviewReplyReplyItem>,
    @Json(name = "review")
    val review: WNReviewReplyReview,
    @Json(name = "topReview")
    val topReview: WNReviewReplyTopReview,
    @Json(name = "total")
    val total: Int,
    @Json(name = "userInfo")
    val userInfo: WNReviewReplyUserInfo
)