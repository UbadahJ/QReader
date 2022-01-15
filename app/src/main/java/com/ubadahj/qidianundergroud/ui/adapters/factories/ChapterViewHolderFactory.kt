package com.ubadahj.qidianundergroud.ui.adapters.factories

import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ubadahj.qidianundergroud.databinding.ChapterBodyItemBinding
import com.ubadahj.qidianundergroud.databinding.ChapterTitleItemBinding
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.utils.ui.inflater
import com.ubadahj.qidianundergroud.utils.ui.visible


enum class ContentViewHolderType {
    TITLE, CONTENTS;

    companion object {
        fun from(viewType: Int): ContentViewHolderType =
            values().associateBy { it.ordinal }[viewType]
                ?: throw IllegalArgumentException("Invalid view type $viewType")
    }
}

abstract class ContentViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: Content, scaleFactor: Float)
}

object ChapterViewHolderFactory {

    fun get(parent: ViewGroup, type: ContentViewHolderType) = when (type) {
        ContentViewHolderType.TITLE -> ContentTitleViewHolder(
            ChapterTitleItemBinding.inflate(parent.inflater, parent, false)
        )
        ContentViewHolderType.CONTENTS -> ContentContentsViewHolder(
            ChapterBodyItemBinding.inflate(parent.inflater, parent, false)
        )
    }

    private class ContentTitleViewHolder(
        private val binding: ChapterTitleItemBinding
    ) : ContentViewHolder(binding) {
        override fun bind(item: Content, scaleFactor: Float) {
            val split = item.title.split(":")
            binding.headerChapterNumber.apply {
                text = split.first()
                visible = split.first() != split.last()
            }
            binding.headerChapterInfo.apply {
                text = split.last()
            }
        }
    }

    private class ContentContentsViewHolder(
        private val binding: ChapterBodyItemBinding
    ) : ContentViewHolder(binding) {
        override fun bind(item: Content, scaleFactor: Float) {
            binding.contents.apply {
                textSize = 16f * scaleFactor
                setTextFuture(
                    PrecomputedTextCompat.getTextFuture(
                        item.contents,
                        TextViewCompat.getTextMetricsParams(binding.contents),
                        null
                    )
                )
            }
        }
    }

}