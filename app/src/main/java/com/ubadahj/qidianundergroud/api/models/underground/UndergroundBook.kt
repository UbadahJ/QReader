package com.ubadahj.qidianundergroud.api.models.underground

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Parcelize
@JsonClass(generateAdapter = true)
data class UndergroundBook(
    @Json(name = "ID") val id: String,
    @Json(name = "Name") val name: String,
    @Json(name = "LastUpdated") val _lastUpdated: Int,
    @Json(name = "Status") val _status: String = "",
) : Parcelable {

    @IgnoredOnParcel
    var lastUpdated: String = Instant.ofEpochSecond(_lastUpdated.toLong())
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"))
        .toString()

    @IgnoredOnParcel
    var completed: Boolean = "Completed" in _status

    @IgnoredOnParcel
    var inLibrary: Boolean = false

    @IgnoredOnParcel
    var lastRead: Int = 0

}
