package com.ubadahj.qidianundergroud.api.models.webnovel.reviews


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewBookReviewInfo(
    @Json(name = "appId")
    val appId: Int,
    @Json(name = "bookId")
    val bookId: String,
    @Json(name = "content")
    val content: String,
    @Json(name = "createTime")
    val createTime: Long,
    @Json(name = "createTimeFormat")
    val createTimeFormat: String,
    @Json(name = "essenceStatus")
    val essenceStatus: Int,
    @Json(name = "headImageId")
    val headImageId: Long,
    @Json(name = "holdBadgeCoverId")
    val holdBadgeCoverId: Long,
    @Json(name = "holdBadgeCoverURL")
    val holdBadgeCoverURL: String,
    @Json(name = "imageItems")
    val imageItems: List<WNReviewImageItem>,
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
    @Json(name = "privilegeInfo")
    val privilegeInfo: WNReviewPrivilegeInfo,
    @Json(name = "replyAmount")
    val replyAmount: Int,
    @Json(name = "reviewId")
    val reviewId: String,
    @Json(name = "status")
    val status: Int,
    @Json(name = "topStatus")
    val topStatus: Boolean,
    @Json(name = "totalScore")
    val totalScore: Float,
    @Json(name = "userId")
    val userId: Long,
    @Json(name = "userLevel")
    val userLevel: Int,
    @Json(name = "userName")
    val userName: String,
    @Json(name = "userRole")
    val userRole: Int
)