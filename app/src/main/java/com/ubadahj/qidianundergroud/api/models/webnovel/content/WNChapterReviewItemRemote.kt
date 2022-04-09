package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNChapterReviewItemRemote(
    @Json(name = "appId")
    val appId: Int,
    @Json(name = "avatar")
    val avatar: Long,
    @Json(name = "badgeInfo")
    val badgeInfo: WNBadgeInfoRemote,
    @Json(name = "commentType")
    val commentType: Int,
    @Json(name = "content")
    val content: String,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "isLiked")
    val isLiked: Int,
    @Json(name = "likeNums")
    val likeNums: Int,
    @Json(name = "pAppId")
    val pAppId: Int,
    @Json(name = "pContent")
    val pContent: String,
    @Json(name = "pPenName")
    val pPenName: String,
    @Json(name = "pStatus")
    val pStatus: Int,
    @Json(name = "pUUT")
    val pUUT: Long,
    @Json(name = "pUserId")
    val pUserId: Long,
    @Json(name = "pUserImg")
    val pUserImg: Long,
    @Json(name = "pUserName")
    val pUserName: String,
    @Json(name = "penName")
    val penName: String,
    @Json(name = "reviewId")
    val reviewId: String,
    @Json(name = "reviewType")
    val reviewType: Int,
    @Json(name = "role")
    val role: Int,
    @Json(name = "status")
    val status: Int,
    @Json(name = "UUT")
    val uUT: Long,
    @Json(name = "userId")
    val userId: Long,
    @Json(name = "userLevel")
    val userLevel: Int,
    @Json(name = "userName")
    val userName: String
)