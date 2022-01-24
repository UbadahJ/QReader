package com.ubadahj.qidianundergroud.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.l4digital.fastscroll.FastScroller
import com.ubadahj.qidianundergroud.ui.adapters.factories.ChapterViewHolderFactory
import com.ubadahj.qidianundergroud.ui.adapters.factories.ContentViewHolder
import com.ubadahj.qidianundergroud.ui.adapters.factories.ContentViewHolderType
import com.ubadahj.qidianundergroud.ui.models.ContentHeaderConfig
import com.ubadahj.qidianundergroud.ui.models.ContentUIItem

class ContentAdapter(
    items: List<ContentUIItem> = listOf(),
    var scaleFactor: () -> Float = { 1f },
    val headerConfig: ContentHeaderConfig,
    val onClick: (ContentUIItem) -> Unit
) : ListAdapter<ContentUIItem, ContentViewHolder>(DiffCallback()),
    FastScroller.SectionIndexer {

    init {
        submitList(items)
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ContentUIItem.ContentUITitleItem -> ContentViewHolderType.TITLE.ordinal
        is ContentUIItem.ContentUIContentItem -> ContentViewHolderType.CONTENTS.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        return ChapterViewHolderFactory.get(
            parent,
            ContentViewHolderType.from(viewType),
            headerConfig
        ) { onClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(getItem(position).content, scaleFactor())
    }

    class DiffCallback : DiffUtil.ItemCallback<ContentUIItem>() {
        override fun areItemsTheSame(oldItem: ContentUIItem, newItem: ContentUIItem): Boolean =
            oldItem.content.id == newItem.content.id

        override fun areContentsTheSame(oldItem: ContentUIItem, newItem: ContentUIItem): Boolean =
            oldItem.content == newItem.content
    }

    override fun getSectionText(position: Int): CharSequence {
        return getItem(position).content.title.split(':').first().trim()
    }

}
