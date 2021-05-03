package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.GroupItemBinding
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.utils.models.*
import com.ubadahj.qidianundergroud.utils.ui.visible

class GroupAdapter(
    private var groups: List<ChapterGroup>,
    private val onClick: (ChapterGroup) -> Unit
) : ListAdapter<ChapterGroup, GroupAdapter.ViewHolder>(DiffCallback()), Filterable {

    private val defaultColors: MutableMap<ChapterGroup, Int> = mutableMapOf()
    private var readColor: Int = 0

    init {
        submitList(groups)
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (readColor == 0)
            readColor = ContextCompat.getColor(
                parent.context, R.color.material_on_surface_disabled
            )

        return ViewHolder(
            GroupItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        ) { onClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.binding.root.context
        val group = getItem(position)
        defaultColors[group] = holder.binding.chapterId.currentTextColor

        var chapterLabel = "Chapter • ${group.firstChapter}"
        if (group.firstChapter != group.lastChapter)
            chapterLabel += " ⁓ ${group.lastChapter}"

        holder.binding.chapterId.text = chapterLabel
        holder.binding.readProgress.text = "➦  Chapter ${group.lastRead}"

        holder.binding.chapterId.setTextColor(
            if (group.isRead()) readColor else defaultColors[group]!!
        )
        holder.binding.downloaderIndicator.setColorFilter(
            if (group.isRead()) readColor else defaultColors[group]!!
        )
        holder.binding.readProgress.visible =
            group.lastRead != 0 && group.lastRead != group.lastChapter
        holder.binding.downloaderIndicator.visible = group.isDownloaded(context)
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
                submitList(p1?.values as? List<ChapterGroup>, true)
            }

        }
    }

    override fun submitList(list: List<ChapterGroup>?) {
        submitList(list, false)
    }

    @Suppress("UNCHECKED_CAST")
    private fun submitList(list: List<Any>?, filtered: Boolean) {
        // This function is responsible for maintaining the
        // actual contents for the list for filtering
        // The submitList for parent class delegates false
        // so that a new contents can be set
        // While a filter pass true which make sure original list
        // is maintained
        if (!filtered) {
            groups = (list ?: listOf()) as List<ChapterGroup>
        }

        super.submitList(list as List<ChapterGroup>?)
    }

    class ViewHolder(val binding: GroupItemBinding, onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.children.iterator().forEach {
                it.setOnClickListener { onClick(bindingAdapterPosition) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChapterGroup>() {
        override fun areItemsTheSame(oldItem: ChapterGroup, newItem: ChapterGroup): Boolean =
            oldItem.link == newItem.link

        override fun areContentsTheSame(oldItem: ChapterGroup, newItem: ChapterGroup): Boolean =
            oldItem == newItem
    }

}