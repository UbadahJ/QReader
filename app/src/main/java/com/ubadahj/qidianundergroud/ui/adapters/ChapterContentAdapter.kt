package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.l4digital.fastscroll.FastScroller
import com.ubadahj.qidianundergroud.databinding.ChapterContentItemBinding
import com.ubadahj.qidianundergroud.models.Chapter

class ChapterContentAdapter(items: List<Chapter>) :
    ListAdapter<Chapter, ChapterContentAdapter.ViewHolder>(DiffCallback()),
    FastScroller.SectionIndexer {

    init {
        submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChapterContentItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.contents.textSize = 16f
        holder.binding.contents.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                getItem(position).contents,
                TextViewCompat.getTextMetricsParams(holder.binding.contents),
                null
            )
        )
    }

    class ViewHolder(val binding: ChapterContentItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<Chapter>() {
        override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean =
            oldItem == newItem
    }

    override fun getSectionText(position: Int): CharSequence {
        return getItem(position).title.split(':').first().trim()
    }

}