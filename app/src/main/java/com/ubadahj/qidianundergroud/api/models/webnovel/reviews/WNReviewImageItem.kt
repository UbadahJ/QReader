package com.ubadahj.qidianundergroud.api.models.webnovel.reviews


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WNReviewImageItem(
    @Json(name = "height")
    val height: Int,
    @Json(name = "imageUrl")
    val imageUrl: String,
    @Json(name = "width")
    val width: Int
)