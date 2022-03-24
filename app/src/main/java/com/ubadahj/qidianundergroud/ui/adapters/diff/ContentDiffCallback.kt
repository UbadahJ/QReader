package com.ubadahj.qidianundergroud.ui.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.ubadahj.qidianundergroud.ui.models.ContentUIItem

object ContentDiffCallback : DiffUtil.ItemCallback<ContentUIItem>() {
    override fun areItemsTheSame(oldItem: ContentUIItem, newItem: ContentUIItem): Boolean =
        oldItem.content.id == newItem.content.id

    override fun areContentsTheSame(oldItem: ContentUIItem, newItem: ContentUIItem): Boolean =
        oldItem.content == newItem.content
}