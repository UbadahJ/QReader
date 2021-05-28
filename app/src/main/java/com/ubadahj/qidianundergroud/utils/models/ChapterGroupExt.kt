package com.ubadahj.qidianundergroud.utils.models

import android.content.Context
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository

val ChapterGroup.firstChapter: Int
    get() = text.split("-").first().trim().toInt()

val ChapterGroup.lastChapter: Int
    get() = text.split("-").last().trim().toInt()

val ChapterGroup.total: Int
    get() = lastChapter - firstChapter + 1

fun ChapterGroup.isRead() = lastRead == lastChapter

fun ChapterGroup.isDownloaded(context: Context) =
    ChapterGroupRepository(context)
        .isDownloaded(this)

operator fun ChapterGroup.contains(chapter: Int): Boolean = chapter in firstChapter..lastChapter
