package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.databinding.LibraryBookItemBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import kotlinx.coroutines.GlobalScope

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LibraryAdapter(
    private var books: List<Book>,
    private val onClick: (Book) -> Unit
) : ListAdapter<Book, LibraryAdapter.ViewHolder>(DiffCallback()), Filterable {

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
        val book = getItem(position)
        holder.binding.bookTitle.text = book.name
        GlobalScope.launch {
            MetadataRepository(holder.binding.bookCover.context)
                .getBook(book)
                .collect { meta ->
                    meta?.coverPath?.let { holder.binding.bookCover.load(it) }
                }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults =
                FilterResults().apply {
                    Timber.d { "getFilter: $books" }
                    values = if (p0.isNullOrEmpty())
                        books
                    else
                        books.filter { it.name.contains(p0, true) }
                }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                submitList(p1?.values as? MutableList<Book>, true)
            }

        }
    }

    override fun submitList(list: List<Book>?) {
        submitList(list, false)
    }

    private fun submitList(list: List<Book>?, filtered: Boolean) {
        // This function is responsible for maintaining the
        // actual contents for the list for filtering
        // The submitList for parent class delegates false
        // so that a new contents can be set
        // While a filter pass true which make sure original list
        // is maintained
        if (!filtered)
            books = list ?: listOf()

        super.submitList(list)
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