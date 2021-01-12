package com.ubadahj.qidianundergroud.api.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ChapterGroupJson(
    @Json(name = "Text") val text: String,
    @Json(name = "Href") val link: String
) : Parcelable

