package com.ubadahj.qidianundergroud.ui.adapters.factories

import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ubadahj.qidianundergroud.databinding.ChapterBodyItemBinding
import com.ubadahj.qidianundergroud.databinding.ChapterTitleItemBinding
import com.ubadahj.qidianundergroud.models.Chapter
import com.ubadahj.qidianundergroud.utils.ui.inflater
import com.ubadahj.qidianundergroud.utils.ui.visible


enum class ChapterViewHolderType {
    TITLE, CONTENTS;

    companion object {
        fun from(viewType: Int): ChapterViewHolderType =
            values().associateBy { it.ordinal }[viewType]
                ?: throw IllegalArgumentException("Invalid view type $viewType")
    }
}

abstract class ChapterViewHolder(
    binding: ViewBinding
) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item: Chapter, scaleFactor: Float)
}

object ChapterViewHolderFactory {

    fun get(parent: ViewGroup, type: ChapterViewHolderType) = when (type) {
        ChapterViewHolderType.TITLE -> ChapterTitleViewHolder(
            ChapterTitleItemBinding.inflate(parent.inflater, parent, false)
        )
        ChapterViewHolderType.CONTENTS -> ChapterContentsViewHolder(
            ChapterBodyItemBinding.inflate(parent.inflater, parent, false)
        )
    }

    private class ChapterTitleViewHolder(
        private val binding: ChapterTitleItemBinding
    ) : ChapterViewHolder(binding) {
        override fun bind(item: Chapter, scaleFactor: Float) {
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

    private class ChapterContentsViewHolder(
        private val binding: ChapterBodyItemBinding
    ) : ChapterViewHolder(binding) {
        override fun bind(item: Chapter, scaleFactor: Float) {
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