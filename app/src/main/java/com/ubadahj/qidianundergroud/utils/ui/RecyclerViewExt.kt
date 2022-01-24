package com.ubadahj.qidianundergroud.utils.ui

import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.linearScroll(pos: Int) {
    post {
        layoutManager?.let {
            (it as LinearLayoutManager).scrollToPositionWithOffset(pos, 0)
        }
    }
}

fun RecyclerView.onDataChangeListener(action: () -> Unit) {
    adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            doOnLayout { action() }
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }
    })
}

fun RecyclerView.preserveState(action: RecyclerView.() -> Unit) {
    val state = layoutManager?.onSaveInstanceState()
    action()
    layoutManager?.onRestoreInstanceState(state)
}