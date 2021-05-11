package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ubadahj.qidianundergroud.databinding.LibraryBookItemBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import com.ubadahj.qidianundergroud.utils.models.isRead
import com.ubadahj.qidianundergroud.utils.repositories.getGroups
import com.ubadahj.qidianundergroud.utils.ui.toDp
import com.ubadahj.qidianundergroud.utils.ui.visible
import kotlinx.coroutines.flow.collect

class LibraryAdapter(
    books: List<Book>,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onClick: (Book) -> Unit
) : FilterableListAdapter<Book, LibraryAdapter.ViewHolder>(DiffCallback()) {

    init {
        submitList(books)
        filterPredicate = { list, constraint ->
            list.filter { it.name.contains(constraint, true) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LibraryBookItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) { onClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.binding.bookCover.context
        val book = getItem(position)
        holder.binding.bookTitle.text = book.name
        lifecycleScope.launchWhenResumed {
            MetadataRepository(context)
                .getBook(book)
                .collect { meta ->
                    meta?.coverPath?.let {
                        holder.binding.bookCover.load(it) {
                            transformations(RoundedCornersTransformation(4.toDp(context).toFloat()))
                        }
                    }
                }
        }
        lifecycleScope.launchWhenResumed {
            book.getGroups(context)
                .collect { group ->
                    val unreadCount = group.filter { !it.isRead() }.size
                    holder.binding.unreadCount.apply {
                        if (unreadCount > 0) {
                            visible = true
                            text = unreadCount.toString()
                        } else {
                            visible = false
                        }
                    }
                }
        }
    }

    class ViewHolder(val binding: LibraryBookItemBinding, onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(bindingAdapterPosition) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
            oldItem == newItem
    }

}