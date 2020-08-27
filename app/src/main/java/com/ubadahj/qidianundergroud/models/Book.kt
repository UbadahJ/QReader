package com.ubadahj.qidianundergroud.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Entity
@JsonClass(generateAdapter = true)
data class Book(
    @PrimaryKey
    @Json(name = "ID") val id: String,
    @ColumnInfo(name = "name")
    @Json(name = "Name") val name: String,
    @ColumnInfo(name = "lastUpdated")
    @Json(name = "LastUpdated") val lastUpdated: Int,
    @ColumnInfo(name = "status")
    @Json(name = "Status") val _status: String = ""
) {

    @ColumnInfo(name = "chapters")
    var chapterGroups: List<ChapterGroup> = listOf()

    @ColumnInfo(name = "lastRead")
    var lastRead: Int = 0

    val formattedLastUpdated: String
        get() = Instant.ofEpochMilli(lastUpdated.toLong())
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            .toString()

    val status: Boolean
        get() = "Completed" in _status
}