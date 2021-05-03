package com.ubadahj.qidianundergroud.utils.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addOnScrollStateListener(listener: (RecyclerView, Int) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            listener(recyclerView, newState)
        }
    })
}

var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
