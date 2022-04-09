package com.ubadahj.qidianundergroud.api.models.webnovel.content


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WNContentRemote(
    @Json(name = "appId")
    val appId: Int,
    @Json(name = "content")
    val content: String,
    @Json(name = "contentAmount")
    val contentAmount: Int,
    @Json(name = "contentId")
    val contentId: String,
    @Json(name = "isLiked")
    val isLiked: Int,
    @Json(name = "likeAmount")
    val likeAmount: Int,
    @Json(name = "paragraphId")
    val paragraphId: String,
    @Json(name = "UUT")
    val uUT: Long,
    @Json(name = "userId")
    val userId: Long,
    @Json(name = "userName")
    val userName: String
)