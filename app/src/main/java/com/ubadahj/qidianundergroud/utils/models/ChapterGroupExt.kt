package com.ubadahj.qidianundergroud.utils.models

import com.ubadahj.qidianundergroud.models.Group
import java.util.*

val Group.firstChapter: Int
    get() = text.split("-").first().trim().toInt()

val Group.lastChapter: Int
    get() = text.split("-").last().trim().toInt()

val Group.total: Int
    get() = lastChapter - firstChapter + 1

fun Group.isRead() = lastRead == lastChapter


val Group.source: String
    get() {
        val source = link.replace(Regex(".+//|www.|\\..+"), "").capitalize(Locale.ROOT)
        return if ("book/" in source) "WebNovel" else source
    }

operator fun Group.contains(chapter: Int): Boolean = chapter in firstChapter..lastChapter
