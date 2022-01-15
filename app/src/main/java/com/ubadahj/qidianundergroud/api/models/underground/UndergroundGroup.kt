package com.ubadahj.qidianundergroud.api.models.underground

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UndergroundGroup(
    @Json(name = "Text") val text: String,
    @Json(name = "Href") val link: String
) : Parcelable
