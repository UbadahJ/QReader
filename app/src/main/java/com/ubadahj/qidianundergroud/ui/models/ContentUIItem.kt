package com.ubadahj.qidianundergroud.ui.models

import com.ubadahj.qidianundergroud.models.Content

sealed class ContentUIItem(
    val content: Content
) {
    class ContentUITitleItem(content: Content) : ContentUIItem(content)
    class ContentUIContentItem(content: Content, val text: String) : ContentUIItem(content)
}