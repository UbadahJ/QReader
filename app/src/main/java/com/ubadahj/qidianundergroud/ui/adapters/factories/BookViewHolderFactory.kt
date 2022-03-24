package com.ubadahj.qidianundergroud.ui.adapters.factories

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import coil.load
import coil.transform.CircleCropTransformation
import com.ubadahj.qidianundergroud.databinding.BookMetaItemBinding
import com.ubadahj.qidianundergroud.databinding.BookNonMetaItemBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.utils.ui.inflater
import com.ubadahj.qidianundergroud.utils.ui.visible


enum class BookViewHolderType {
    NON_META, META;

    companion object {
        fun from(viewType: Int): BookViewHolderType =
            values().associateBy { it.ordinal }[viewType]
                ?: throw IllegalArgumentException("Invalid view type $viewType")
    }
}

abstract class BookViewHolder(
    binding: ViewBinding,
    protected val onClick: (Int) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener { onClick(bindingAdapterPosition) }
    }

    abstract fun bind(item: Book)
}

object BookViewHolderFactory {

    fun get(parent: ViewGroup, type: BookViewHolderType, onClick: (Int) -> Unit) = when (type) {
        BookViewHolderType.NON_META -> NonMetaBookViewHolder(
            BookNonMetaItemBinding.inflate(parent.inflater, parent, false),
            onClick
        )
        BookViewHolderType.META -> MetaBookViewHolder(
            BookMetaItemBinding.inflate(parent.inflater, parent, false),
            onClick
        )
    }

    private class NonMetaBookViewHolder(
        private val binding: BookNonMetaItemBinding,
        onClick: (Int) -> Unit
    ) : BookViewHolder(binding, onClick) {
        override fun bind(item: Book) {
            binding.bookTitle.text = item.name
            binding.completedText.visible = item.completed
        }
    }

    private class MetaBookViewHolder(
        private val binding: BookMetaItemBinding,
        onClick: (Int) -> Unit
    ) : BookViewHolder(binding, onClick) {
        override fun bind(item: Book) {
            item.coverPath?.let {
                binding.bookCover.load(it) {
                    transformations(CircleCropTransformation())
                }
            }
            binding.bookTitle.text = item.name
            binding.authorName.text = item.author
            binding.ratingText.text = "★ ${item.rating}"
            binding.genreText.text = item.category
            binding.completedText.visible = item.completed
        }
    }

}