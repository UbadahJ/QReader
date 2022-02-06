package com.ubadahj.qidianundergroud.ui.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.ubadahj.qidianundergroud.models.Group

object GroupDiffCallback : DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean =
        oldItem.link == newItem.link

    override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean =
        oldItem == newItem
}