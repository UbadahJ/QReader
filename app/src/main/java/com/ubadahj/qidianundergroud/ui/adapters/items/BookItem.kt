package com.ubadahj.qidianundergroud.ui.adapters.items

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookItemBinding
import com.ubadahj.qidianundergroud.models.Book

class BookItem(val book: Book) : AbstractBindingItem<BookItemBinding>() {
    override val type: Int
        get() = R.id.book_item

    override fun bindView(binding: BookItemBinding, payloads: List<Any>) {
        binding.textView.text = book.name
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): BookItemBinding =
        BookItemBinding.inflate(inflater, parent, false)
}