package com.ubadahj.qidianundergroud.api.models.webnovel.reviews


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewData(
    @Json(name = "baseInfo")
    val baseInfo: WNReviewBaseInfo,
    @Json(name = "basePrivilegeUrl")
    val basePrivilegeUrl: String,
    @Json(name = "bookReviewInfos")
    val bookReviewInfos: List<WNReviewBookReviewInfo>,
    @Json(name = "bookStatisticsInfo")
    val bookStatisticsInfo: WNReviewBookStatisticsInfo,
    @Json(name = "isLast")
    val isLast: Int,
    @Json(name = "reviewNum")
    val reviewNum: Int,
    @Json(name = "userInfo")
    val userInfo: WNReviewUserInfo
)