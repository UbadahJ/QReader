package com.ubadahj.qidianundergroud.ui.adapters

import android.view.ViewGroup
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.ui.adapters.diff.BookDiffCallback
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolder
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolderFactory
import com.ubadahj.qidianundergroud.ui.adapters.factories.BookViewHolderType
import com.ubadahj.qidianundergroud.ui.adapters.generic.SortableListAdapter
import com.ubadahj.qidianundergroud.utils.ui.getItemSafely

class BookAdapter(
    books: List<Book>,
    private val onClick: (Book) -> Unit
) : SortableListAdapter<Book, BookViewHolder>(BookDiffCallback) {

    override var sortedBy: (Book) -> Comparable<*>? = Book::name
    override val filterPredicate: (List<Book>, String) -> List<Book> =
        { list, constraint -> list.filter { it.name.contains(constraint, true) } }

    override var bubbleText: ((Book) -> String) = { it.name.first().toString() }
        private set

    init {
        submitList(books)
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).author != null) 1 else 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        return BookViewHolderFactory.get(parent, BookViewHolderType.from(viewType)) {
            getItemSafely(it, onClick)
        }
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
