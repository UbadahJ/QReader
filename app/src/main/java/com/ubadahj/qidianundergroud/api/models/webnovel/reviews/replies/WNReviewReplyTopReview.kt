package com.ubadahj.qidianundergroud.api.models.webnovel.reviews.replies


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewReplyTopReview(
    @Json(name = "appId")
    val appId: Int,
    @Json(name = "bookId")
    val bookId: Int,
    @Json(name = "bookName")
    val bookName: String,
    @Json(name = "content")
    val content: String,
    @Json(name = "coverId")
    val coverId: Int,
    @Json(name = "createTime")
    val createTime: Int,
    @Json(name = "createTimeFormat")
    val createTimeFormat: String,
    @Json(name = "essenceStatus")
    val essenceStatus: Int,
    @Json(name = "headImageId")
    val headImageId: Int,
    @Json(name = "holdBadgeCoverId")
    val holdBadgeCoverId: Int,
    @Json(name = "holdBadgeCoverURL")
    val holdBadgeCoverURL: String,
    @Json(name = "imageItems")
    val imageItems: Any,
    @Json(name = "isLiked")
    val isLiked: Int,
    @Json(name = "isLikedByAuthor")
    val isLikedByAuthor: Int,
    @Json(name = "isViceModerator")
    val isViceModerator: Int,
    @Json(name = "leakFlag")
    val leakFlag: Int,
    @Json(name = "likeAmount")
    val likeAmount: Int,
    @Json(name = "pContent")
    val pContent: String,
    @Json(name = "pImageItems")
    val pImageItems: Any,
    @Json(name = "pReviewId")
    val pReviewId: Int,
    @Json(name = "pStatus")
    val pStatus: Int,
    @Json(name = "pUserName")
    val pUserName: String,
    @Json(name = "privilegeInfo")
    val privilegeInfo: Any,
    @Json(name = "replyAmount")
    val replyAmount: Int,
    @Json(name = "reviewId")
    val reviewId: Int,
    @Json(name = "status")
    val status: Int,
    @Json(name = "topStatus")
    val topStatus: Boolean,
    @Json(name = "totalScore")
    val totalScore: Int,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "userLevel")
    val userLevel: Int,
    @Json(name = "userName")
    val userName: String,
    @Json(name = "userRole")
    val userRole: Int
)