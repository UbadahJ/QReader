package com.ubadahj.qidianundergroud.ui.adapters

import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterItem

class ChapterAdapter(val book: Book) : ItemAdapter<ChapterItem>() {

    init {
        add(book.chapterGroups.map { ChapterItem(book, it) })
    }

}