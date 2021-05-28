package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.GroupItemBinding
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.utils.models.contains
import com.ubadahj.qidianundergroud.utils.models.firstChapter
import com.ubadahj.qidianundergroud.utils.models.isRead
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.ui.visible

class GroupAdapter(
    groups: List<ChapterGroup>,
    private val onClick: (ChapterGroup) -> Unit,
    private val menuClick: (ChapterGroup) -> Unit
) : FilterableListAdapter<ChapterGroup, GroupAdapter.ViewHolder>(DiffCallback()) {

    private val defaultColors: MutableMap<ChapterGroup, Int> = mutableMapOf()
    private var readColor: Int = 0

    override val filterPredicate: (List<ChapterGroup>, String) -> List<ChapterGroup> =
        { list, constraint ->
            list.filter { it.contains(constraint.toIntOrNull() ?: -1) }
        }

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
        holder.binding.menu.setColorFilter(
            if (group.isRead()) readColor else defaultColors[group]!!
        )
        holder.binding.readProgress.visible =
            group.lastRead != 0 && group.lastRead != group.lastChapter

        holder.binding.menu.setOnClickListener { menuClick(group) }
    }

    class ViewHolder(val binding: GroupItemBinding, onClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.children.toMutableList()
                .apply { add(binding.root) }
                .forEach {
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
