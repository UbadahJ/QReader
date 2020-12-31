package com.ubadahj.qidianundergroud.utils.ui

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.addOnScrollStateListener(listener: (RecyclerView, Int) -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            listener(recyclerView, newState)
        }
    })
}
