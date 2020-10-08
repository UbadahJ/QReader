package com.ubadahj.qidianundergroud.utils.models

import com.ubadahj.qidianundergroud.models.ChapterGroup

val ChapterGroup.firstChapter: Int
    get() = text.split("-").first().trim().toInt()

val ChapterGroup.lastChapter: Int
    get() = text.split("-").last().trim().toInt()

val ChapterGroup.total: Int
    get() = lastChapter - firstChapter + 1

operator fun ChapterGroup.contains(chapter: Int): Boolean = chapter in firstChapter..lastChapter