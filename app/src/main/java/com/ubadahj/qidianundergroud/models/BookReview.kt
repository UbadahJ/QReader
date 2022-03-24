package com.ubadahj.qidianundergroud.models

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class BookReview(
    val userId: Long,
    val bookId: String,
    val reviewId: String,
    val userName: String,
    val rating: Float,
    val contents: String,
    val timestamp: Long,
    val likes: Int,
    val replies: Int
) {
    val date: LocalDate = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val userAvatar: String = "https://user-pic.webnovel.com/userheadimg/${userId}-10/100.jpg"
}