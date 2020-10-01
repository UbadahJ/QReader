package com.ubadahj.qidianundergroud.ui.adapters.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.MenuItemBinding

typealias MenuAdapterItem = MenuItem

class MenuItem(val text: String, @DrawableRes val icon: Int = 0) :
    AbstractBindingItem<MenuItemBinding>() {

    override val type: Int
        get() = R.id.menu_item

    override fun bindView(binding: MenuItemBinding, payloads: List<Any>) {
        binding.menuItem.text = text
        if (icon != 0)
            binding.menuItem.icon = ContextCompat.getDrawable(binding.root.context, icon)
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): MenuItemBinding =
        MenuItemBinding.inflate(inflater, parent, false)

}