package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNBadgeInfoRemote(
    @Json(name = "badgeId")
    val badgeId: Int,
    @Json(name = "badgeName")
    val badgeName: String,
    @Json(name = "badgeType")
    val badgeType: String,
    @Json(name = "baseUrl")
    val baseUrl: String,
    @Json(name = "maxGrade")
    val maxGrade: Int,
    @Json(name = "updateTime")
    val updateTime: Long
)