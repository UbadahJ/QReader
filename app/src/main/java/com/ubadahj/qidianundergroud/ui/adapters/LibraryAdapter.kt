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
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Metadata
import com.ubadahj.qidianundergroud.utils.models.isRead
import com.ubadahj.qidianundergroud.utils.ui.toDp
import com.ubadahj.qidianundergroud.utils.ui.visible

class LibraryAdapter(
    books: List<Book>,
    private val groupSupplier: suspend (Book) -> List<Group>,
    private val metadataSupplier: suspend (Book) -> Metadata?,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onClick: (Book) -> Unit
) : FilterableListAdapter<Book, LibraryAdapter.ViewHolder>(DiffCallback()) {

    override val filterPredicate: (List<Book>, String) -> List<Book> = { list, constraint ->
        list.filter { it.name.contains(constraint, true) }
    }

    override val bubbleText: ((Book) -> String) = { it.name.first().toString() }

    init {
        submitList(books)
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
            metadataSupplier(book)?.coverPath?.let {
                holder.binding.bookCover.load(it) {
                    transformations(RoundedCornersTransformation(4.toDp(context).toFloat()))
                }
            }
        }
        lifecycleScope.launchWhenResumed {
            val group = groupSupplier(book)
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
