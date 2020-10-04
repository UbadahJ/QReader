package com.ubadahj.qidianundergroud.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Parcelize
@JsonClass(generateAdapter = true)
data class Book(
    @Json(name = "ID") val id: String,
    @Json(name = "Name") val name: String,
    @Json(name = "LastUpdated") val lastUpdated: Int,
    @Json(name = "Status") val _status: String = ""
) : Parcelable {

    @Json(name = "InLibrary")
    var inLibrary: Boolean = false

    @Json(name = "Chapters")
    var chapterGroups: List<ChapterGroup> = listOf()

    @Json(name = "LastRead")
    var lastRead: Int = 0

    val formattedLastUpdated: String
        get() = Instant.ofEpochSecond(lastUpdated.toLong())
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))
            .toString()

    val status: Boolean
        get() = "Completed" in _status
}