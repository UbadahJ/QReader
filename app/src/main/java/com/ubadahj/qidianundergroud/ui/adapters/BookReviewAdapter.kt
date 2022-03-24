package com.ubadahj.qidianundergroud.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookReviewItemBinding
import com.ubadahj.qidianundergroud.models.BookReview
import com.ubadahj.qidianundergroud.ui.adapters.diff.BookReviewDiffCallback
import com.ubadahj.qidianundergroud.ui.adapters.generic.FilterableListAdapter
import com.ubadahj.qidianundergroud.utils.ui.inflater

class BookReviewAdapter :
    FilterableListAdapter<BookReview, BookReviewAdapter.ViewHolder>(BookReviewDiffCallback) {

    override val filterPredicate: (list: List<BookReview>, constraint: String) -> List<BookReview> =
        { list, constraint -> list.filter { constraint in it.userName || constraint in it.contents } }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BookReviewItemBinding.inflate(parent.inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            userName.text = item.userName
            userAvatar.load(item.userAvatar) {
                placeholder(R.drawable.placeholder)
                transformations(CircleCropTransformation())
            }
            userReview.text = item.contents
            createdDate.text = "${item.date.monthValue} months"
            reviewRating.rating = item.rating.toFloat()
        }
    }

    class ViewHolder(val binding: BookReviewItemBinding) : RecyclerView.ViewHolder(binding.root)
}