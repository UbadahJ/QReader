package com.ubadahj.qidianundergroud.ui.models

import com.ubadahj.qidianundergroud.models.Chapter

sealed class ChapterUIItem(
    val chapter: Chapter
) {
    class ChapterUITitleItem(chapter: Chapter) : ChapterUIItem(chapter)
    class ChapterUIContentItem(chapter: Chapter) : ChapterUIItem(chapter)
}