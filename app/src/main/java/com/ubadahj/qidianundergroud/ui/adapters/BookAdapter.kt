package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.databinding.BookItemBinding
import com.ubadahj.qidianundergroud.models.Book

class BookAdapter(
        private var books: List<Book>,
        private val onClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.ViewHolder>(DiffCallback()), Filterable {

    init {
        submitList(books)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BookItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
        )) { onClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textView.text = getItem(position).name
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

    class ViewHolder(val binding: BookItemBinding, onClick: (Int) -> Unit) :
            RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(adapterPosition) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
                oldItem == newItem
    }

}