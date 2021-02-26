package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterItemBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.utils.models.contains

class ChapterAdapter(
    private var book: Book,
    private var groups: List<ChapterGroup>,
    private val onClick: (ChapterGroup) -> Unit
) : ListAdapter<ChapterGroup, ChapterAdapter.ViewHolder>(DiffCallback()), Filterable {

    private val defaultColors: MutableMap<ChapterGroup, Int> = mutableMapOf()
    private var highlightColor: Int = 0

    init {
        submitList(book, groups)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (highlightColor == 0)
            highlightColor = ContextCompat.getColor(parent.context, R.color.blue_500)

        return ViewHolder(
            ChapterItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) { onClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = getItem(position)
        defaultColors[group] = holder.binding.chapterId.currentTextColor

        holder.binding.chapterId.text = getItem(position).text
        holder.binding.chapterId.setTextColor(
            if (book.lastRead in group) highlightColor else defaultColors[group]!!
        )
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults =
                FilterResults().apply {
                    Timber.d { "getFilter: $groups" }
                    values = if (p0.toString().toIntOrNull() == null)
                        groups
                    else
                        groups.filter { it.contains(p0.toString().toInt()) }
                }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                submitList(book, p1?.values as? MutableList<ChapterGroup>, true)
            }

        }
    }

    override fun submitList(list: List<ChapterGroup>?) {
        submitList(book, list, false)
    }

    fun submitList(book: Book, list: List<ChapterGroup>?) {
        submitList(book, list, false)
    }

    private fun submitList(book: Book, list: List<ChapterGroup>?, filtered: Boolean) {
        // This function is responsible for maintaining the
        // actual contents for the list for filtering
        // The submitList for parent class delegates false
        // so that a new contents can be set
        // While a filter pass true which make sure original list
        // is maintained
        if (!filtered) {
            this.book = book
            groups = list ?: listOf()
        }

        super.submitList(list)
    }

    class ViewHolder(val binding: ChapterItemBinding, onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(bindingAdapterPosition) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChapterGroup>() {
        override fun areItemsTheSame(oldItem: ChapterGroup, newItem: ChapterGroup): Boolean =
            oldItem.link == newItem.link

        override fun areContentsTheSame(oldItem: ChapterGroup, newItem: ChapterGroup): Boolean =
            oldItem == newItem
    }

}