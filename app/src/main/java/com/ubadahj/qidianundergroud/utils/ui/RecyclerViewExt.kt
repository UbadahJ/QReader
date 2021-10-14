package com.ubadahj.qidianundergroud.utils.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.linearScroll(pos: Int) {
    post {
        layoutManager?.let {
            (it as LinearLayoutManager).scrollToPositionWithOffset(pos, 0)
        }
    }
}

fun RecyclerView.preserveState(action: RecyclerView.() -> Unit) {
    val state = layoutManager?.onSaveInstanceState()
    action()
    layoutManager?.onRestoreInstanceState(state)
}