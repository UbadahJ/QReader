package com.ubadahj.qidianundergroud.models

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chapter(
     @Json(name = "Title") val title: String,
     @Json(name = "Text") val text: String
) : Parcelable