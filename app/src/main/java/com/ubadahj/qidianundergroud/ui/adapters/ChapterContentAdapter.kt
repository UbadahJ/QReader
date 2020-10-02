package com.ubadahj.qidianundergroud.ui.adapters

import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterContentItem

class ChapterContentAdapter(items: List<ChapterContentItem>) : ItemAdapter<ChapterContentItem>() {
    init {
        add(items)
    }
}