package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.databinding.BookItemBinding
import com.ubadahj.qidianundergroud.models.Book

class BookAdapter(
    books: List<Book>,
    private val onClick: (Book) -> Unit
) : FilterableListAdapter<Book, BookAdapter.ViewHolder>(DiffCallback()) {

    override val filterPredicate: (List<Book>, String) -> List<Book> = { list, constraint ->
        list.filter { it.name.contains(constraint, true) }
    }

    override val bubbleText: ((Book) -> String) = { it.name.first().toString() }

    init {
        submitList(books)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            BookItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) { onClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textView.text = getItem(position).name
    }

    class ViewHolder(val binding: BookItemBinding, onClick: (Int) -> Unit) :
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
