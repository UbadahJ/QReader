package com.ubadahj.qidianundergroud.ui.adapters.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterContentItemBinding

class ChapterContentItem(val chapterName: CharSequence, val text: CharSequence) :
    AbstractBindingItem<ChapterContentItemBinding>() {

    override val type: Int
        get() = R.id.title

    override fun bindView(binding: ChapterContentItemBinding, payloads: List<Any>) {
        binding.contents.textSize = 14f
        binding.contents.setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                text,
                TextViewCompat.getTextMetricsParams(binding.contents),
                null
            )
        )
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ChapterContentItemBinding =
        ChapterContentItemBinding.inflate(inflater, parent, false)
}