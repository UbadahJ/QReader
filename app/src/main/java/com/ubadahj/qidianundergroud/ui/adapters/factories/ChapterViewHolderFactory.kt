package com.ubadahj.qidianundergroud.ui.adapters.factories

import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.ubadahj.qidianundergroud.databinding.ChapterBodyItemBinding
import com.ubadahj.qidianundergroud.databinding.ChapterTitleItemBinding
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.ui.adapters.decorations.StickyViewHolder
import com.ubadahj.qidianundergroud.ui.models.ContentHeaderConfig
import com.ubadahj.qidianundergroud.ui.models.ContentUIItem
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

    fun header(
        binding: ChapterTitleItemBinding,
        config: ContentHeaderConfig
    ): StickyViewHolder<ContentUIItem> = ContentTitleViewHolder(binding, config)

    fun get(
        parent: ViewGroup,
        type: ContentViewHolderType,
        config: ContentHeaderConfig,
        onClick: (Int) -> Unit
    ) = when (type) {
        ContentViewHolderType.TITLE -> ContentTitleViewHolder(
            ChapterTitleItemBinding.inflate(parent.inflater, parent, false), config
        )
        ContentViewHolderType.CONTENTS -> ContentContentsViewHolder(
            ChapterBodyItemBinding.inflate(parent.inflater, parent, false), onClick
        )
    }

    private class ContentTitleViewHolder(
        private val binding: ChapterTitleItemBinding,
        private val config: ContentHeaderConfig
    ) : ContentViewHolder(binding), StickyViewHolder<ContentUIItem> {
        init {
            binding.headerBack.setOnClickListener { config.onBackPressed() }
        }
        override val type: Int = ContentViewHolderType.TITLE.ordinal
        override val root = binding.root
        override fun bind(item: ContentUIItem) = bind(item.content, 1f)
        override fun bind(item: Content, scaleFactor: Float) {
            val split = item.title.split(":")
            binding.headerChapterNumber.apply {
                text = split.first()
                visible = split.first() != split.last()
            }
            binding.headerChapterInfo.apply {
                text = split.last().trim()
            }
            binding.headerMenu.setOnClickListener { config.onMenuPressed(item) }
        }
    }

    private class ContentContentsViewHolder(
        private val binding: ChapterBodyItemBinding,
        onClick: (Int) -> Unit
    ) : ContentViewHolder(binding) {
        init {
            listOf(binding.root, binding.contents).forEach {
                it.setOnClickListener { onClick(absoluteAdapterPosition) }
            }
        }

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