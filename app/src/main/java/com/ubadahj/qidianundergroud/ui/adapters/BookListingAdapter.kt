package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.databinding.BookItemBinding
import com.ubadahj.qidianundergroud.models.Book

class BookListingAdapter(private val books: List<Book>, private val onClick: (Book) -> Unit) :
    RecyclerView.Adapter<BookListingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, books, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textView.text = books[position].name
    }

    override fun getItemCount(): Int = books.size

    class ViewHolder(val binding: BookItemBinding, books: List<Book>, onClick: (Book) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClick(books[adapterPosition])
            }
        }
    }

}