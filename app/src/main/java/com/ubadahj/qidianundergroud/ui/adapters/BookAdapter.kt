package com.ubadahj.qidianundergroud.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Metadata
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolder
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolderFactory
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolderType

typealias BookWithMetadata = Pair<Book, Metadata?>

class BookAdapter(
    books: List<BookWithMetadata>,
    private val onClick: (Book) -> Unit
) : FilterableListAdapter<BookWithMetadata, BookViewHolder>(DiffCallback()) {

    override val filterPredicate: (List<BookWithMetadata>, String) -> List<BookWithMetadata> =
        { list, constraint -> list.filter { it.first.name.contains(constraint, true) } }

    override val bubbleText: ((BookWithMetadata) -> String) = { it.first.name.first().toString() }

    init {
        submitList(books)
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).second != null) 1 else 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        return BookViewHolderFactory.get(parent, BookViewHolderType.from(viewType)) {
            onClick(getItem(it).first)
        }
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun <R : Comparable<R>> sortBy(predicate: (BookWithMetadata) -> R?) =
        submitList(currentList.sortedBy(predicate))

    fun reverse() = submitList(currentList.reversed())

    class DiffCallback : DiffUtil.ItemCallback<BookWithMetadata>() {
        override fun areItemsTheSame(
            oldItem: BookWithMetadata,
            newItem: BookWithMetadata
        ): Boolean = oldItem.first.id == newItem.first.id

        override fun areContentsTheSame(
            oldItem: BookWithMetadata,
            newItem: BookWithMetadata
        ): Boolean = oldItem == newItem
    }

}
