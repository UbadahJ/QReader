package com.ubadahj.qidianundergroud.api.models.webnovel.reviews


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewBaseInfo(
    @Json(name = "authorName") val authorName: String,
    @Json(name = "basePrivilegeUrl") val basePrivilegeUrl: String,
    @Json(name = "bookCoverId") val bookCoverId: Long,
    @Json(name = "bookId") val bookId: String,
    @Json(name = "bookName") val bookName: String,
    @Json(name = "description") val description: String,
    @Json(name = "languageCode") val languageCode: Int,
    @Json(name = "languageName") val languageName: String,
    @Json(name = "novelType") val novelType: Int,
    @Json(name = "pvNum") val pvNum: Int,
    @Json(name = "reviewAmount") val reviewAmount: Int
)