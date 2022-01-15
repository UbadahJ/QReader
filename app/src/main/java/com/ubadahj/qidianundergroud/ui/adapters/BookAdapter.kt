package com.ubadahj.qidianundergroud.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolder
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolderFactory
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolderType

class BookAdapter(
    books: List<Book>,
    private val onClick: (Book) -> Unit
) : FilterableListAdapter<Book, BookViewHolder>(DiffCallback()) {

    override val filterPredicate: (List<Book>, String) -> List<Book> =
        { list, constraint -> list.filter { it.name.contains(constraint, true) } }

    override var bubbleText: ((Book) -> String) = { it.name.first().toString() }
        private set

    init {
        submitList(books)
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).novelId != null) 1 else 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        return BookViewHolderFactory.get(parent, BookViewHolderType.from(viewType)) {
            onClick(getItem(it))
        }
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun <R : Comparable<R>> sortBy(predicate: (Book) -> R?) {
        submitList(currentList.sortedBy(predicate))
        bubbleText = { predicate(it).toString() }
    }

    fun reverse() = submitList(currentList.reversed())

    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Book,
            newItem: Book
        ): Boolean = oldItem == newItem
    }

}
