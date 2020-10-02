package com.ubadahj.qidianundergroud.ui.adapters

import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.MenuItem

class MenuAdapter(items: List<MenuItem>) : ItemAdapter<MenuItem>() {

    init {
        super.add(items)
    }

}