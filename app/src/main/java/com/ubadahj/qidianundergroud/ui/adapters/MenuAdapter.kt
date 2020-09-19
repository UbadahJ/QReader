package com.ubadahj.qidianundergroud.ui.adapters

import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.MenuItem

class MenuAdapter(items: List<String>) : ItemAdapter<MenuItem>() {

    init {
        super.add(items.map { MenuItem(it) })
    }

}