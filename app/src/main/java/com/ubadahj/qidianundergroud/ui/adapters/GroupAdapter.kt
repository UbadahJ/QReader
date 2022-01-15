package com.ubadahj.qidianundergroud.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.GroupItemBinding
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.utils.models.*
import com.ubadahj.qidianundergroud.utils.ui.visible

class GroupAdapter(
    groups: List<Group>,
    private val onClick: (Group) -> Unit,
    private val menuClick: (Group) -> Unit
) : FilterableListAdapter<Group, GroupAdapter.ViewHolder>(DiffCallback()) {

    private val defaultColors: MutableMap<Group, Int> = mutableMapOf()
    private var readColor: Int = 0

    override val filterPredicate: (List<Group>, String) -> List<Group> =
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
        holder.binding.chapterSource.text = group.source

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

    class DiffCallback : DiffUtil.ItemCallback<Group>() {
        override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean =
            oldItem.link == newItem.link

        override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean =
            oldItem == newItem
    }

}
