package com.ubadahj.qidianundergroud.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.l4digital.fastscroll.FastScroller
import com.ubadahj.qidianundergroud.ui.adapters.factories.ChapterViewHolder
import com.ubadahj.qidianundergroud.ui.adapters.factories.ChapterViewHolderFactory
import com.ubadahj.qidianundergroud.ui.adapters.factories.ChapterViewHolderType
import com.ubadahj.qidianundergroud.ui.models.ChapterUIItem

class ChapterAdapter(
    items: List<ChapterUIItem>,
    var scaleFactor: () -> Float
) : ListAdapter<ChapterUIItem, ChapterViewHolder>(DiffCallback()),
    FastScroller.SectionIndexer {

    init {
        submitList(items)
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ChapterUIItem.ChapterUITitleItem -> ChapterViewHolderType.TITLE.ordinal
        is ChapterUIItem.ChapterUIContentItem -> ChapterViewHolderType.CONTENTS.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChapterViewHolder {
        return ChapterViewHolderFactory.get(parent, ChapterViewHolderType.from(viewType))
    }

    override fun onBindViewHolder(holder: ChapterViewHolder, position: Int) {
        holder.bind(getItem(position).chapter, scaleFactor())
    }

    class DiffCallback : DiffUtil.ItemCallback<ChapterUIItem>() {
        override fun areItemsTheSame(oldItem: ChapterUIItem, newItem: ChapterUIItem): Boolean =
            oldItem.chapter.id == newItem.chapter.id

        override fun areContentsTheSame(oldItem: ChapterUIItem, newItem: ChapterUIItem): Boolean =
            oldItem.chapter == newItem.chapter
    }

    override fun getSectionText(position: Int): CharSequence {
        return getItem(position).chapter.title.split(':').first().trim()
    }

}
