package com.ubadahj.qidianundergroud.ui.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.ubadahj.qidianundergroud.models.Book

object BookDiffCallback : DiffUtil.ItemCallback<Book>() {
    override fun areItemsTheSame(
        oldItem: Book,
        newItem: Book
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Book,
        newItem: Book
    ): Boolean = oldItem == newItem
}