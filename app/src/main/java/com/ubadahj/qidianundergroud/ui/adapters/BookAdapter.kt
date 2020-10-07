package com.ubadahj.qidianundergroud.ui.adapters

import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.ui.adapters.items.BookItem

class BookAdapter(books: List<Book>) : ItemAdapter<BookItem>() {

    init {
        super.add(books.map { BookItem(it) })
        itemFilter.filterPredicate = { item, constraint ->
            item.book.name.contains(constraint.toString(), true)
        }
    }

}