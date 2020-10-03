package com.ubadahj.qidianundergroud.ui.adapters.items

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterContentItemBinding

class ChapterContentItem(val chapterName: CharSequence, private val text: CharSequence) :
    AbstractBindingItem<ChapterContentItemBinding>() {

    override val type: Int
        get() = R.id.title

    override fun bindView(binding: ChapterContentItemBinding, payloads: List<Any>) {
        binding.contents.text = text
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ChapterContentItemBinding =
        ChapterContentItemBinding.inflate(inflater, parent, false)
}