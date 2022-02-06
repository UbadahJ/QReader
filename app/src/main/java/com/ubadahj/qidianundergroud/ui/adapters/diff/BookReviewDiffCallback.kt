package com.ubadahj.qidianundergroud.ui.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.ubadahj.qidianundergroud.models.BookReview

object BookReviewDiffCallback : DiffUtil.ItemCallback<BookReview>() {
    override fun areItemsTheSame(oldItem: BookReview, newItem: BookReview): Boolean {
        return oldItem.reviewId == newItem.reviewId
    }

    override fun areContentsTheSame(oldItem: BookReview, newItem: BookReview): Boolean {
        return oldItem == newItem
    }
}