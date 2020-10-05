package com.ubadahj.qidianundergroud.ui.adapters.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterItemBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.utils.models.contains

class ChapterItem(val book: Book, val chapter: ChapterGroup) :
    AbstractBindingItem<ChapterItemBinding>() {

    companion object {
        private var defaultColor: Int = 0
        private var highlightColor: Int = 0
    }

    override val type: Int
        get() = R.id.chapter_id

    override fun bindView(binding: ChapterItemBinding, payloads: List<Any>) {
        binding.chapterId.text = chapter.text
        binding.chapterId.setTextColor(
            if (book.lastRead in chapter) highlightColor else defaultColor
        )
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ChapterItemBinding {
        val binding = ChapterItemBinding.inflate(inflater, parent, false)

        if (defaultColor == 0)
            defaultColor = binding.root.currentTextColor
        if (highlightColor == 0)
            highlightColor = ContextCompat.getColor(binding.root.context, R.color.secondaryColor)

        return binding
    }

}