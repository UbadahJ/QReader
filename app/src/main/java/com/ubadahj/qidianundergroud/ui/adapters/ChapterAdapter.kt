package com.ubadahj.qidianundergroud.ui.adapters

import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterItem

class ChapterAdapter(val book: Book, val groups: List<ChapterGroup>) : ItemAdapter<ChapterItem>() {

    init {
        add(groups.map { ChapterItem(book, it) })
    }

}