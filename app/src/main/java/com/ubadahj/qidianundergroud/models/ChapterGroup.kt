package com.ubadahj.qidianundergroud.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChapterGroup(
    @Json(name = "Text") val text: String,
    @Json(name = "Href") val link: String
) {
    val firstChapter: Int
        get() = text.split("-").first().trim().toInt()

    val lastChapter: Int
        get() = text.split("-").last().trim().toInt()

    val total: Int
        get() = lastChapter - firstChapter + 1

    operator fun contains(chapter: Int): Boolean = chapter in firstChapter..lastChapter
}