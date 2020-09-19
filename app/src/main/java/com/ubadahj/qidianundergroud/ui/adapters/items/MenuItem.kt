package com.ubadahj.qidianundergroud.ui.adapters.items

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.MenuItemBinding

class MenuItem(val text: String) : AbstractBindingItem<MenuItemBinding>() {

    override val type: Int
        get() = R.id.menu_item

    override fun bindView(binding: MenuItemBinding, payloads: List<Any>) {
        binding.menuItem.text = text
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): MenuItemBinding =
        MenuItemBinding.inflate(inflater, parent, false)

}