package com.ubadahj.qidianundergroud.ui.adapters

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.databinding.MenuItemBinding
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.ui.getItemSafely
import com.ubadahj.qidianundergroud.utils.ui.inflater

class MenuAdapter(
    items: List<MenuDialogItem>
) : ListAdapter<MenuDialogItem, MenuAdapter.MenuViewHolder>(DiffCallback()) {

    var postOnClick: (() -> Unit)? = null

    init {
        submitList(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(MenuItemBinding.inflate(parent.inflater, parent, false))
        { pos ->
            getItemSafely(pos) {
                it.onClick?.invoke()
                postOnClick?.invoke()
            }
        }
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.menuItem.text = item.text
        if (item.icon != 0)
            holder.binding.menuItem.icon =
                ContextCompat.getDrawable(holder.binding.root.context, item.icon)
    }

    class MenuViewHolder(
        val binding: MenuItemBinding,
        onClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { onClick(bindingAdapterPosition) }
        }
    }
}

private class DiffCallback : DiffUtil.ItemCallback<MenuDialogItem>() {
    override fun areItemsTheSame(oldItem: MenuDialogItem, newItem: MenuDialogItem): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: MenuDialogItem, newItem: MenuDialogItem): Boolean {
        return oldItem.icon == newItem.icon
    }
}
