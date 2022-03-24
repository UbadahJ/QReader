package com.ubadahj.qidianundergroud.api.models.webnovel.reviews


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewBookStatisticsInfo(
    @Json(name = "bookName")
    val bookName: String,
    @Json(name = "bookType")
    val bookType: Int,
    @Json(name = "characterDesign")
    val characterDesign: Double,
    @Json(name = "storyDevelopment")
    val storyDevelopment: Double,
    @Json(name = "totalReviewNum")
    val totalReviewNum: Int,
    @Json(name = "totalScore")
    val totalScore: Double,
    @Json(name = "translationQuality")
    val translationQuality: Double,
    @Json(name = "updatingStability")
    val updatingStability: Double,
    @Json(name = "worldBackGround")
    val worldBackGround: Double
)