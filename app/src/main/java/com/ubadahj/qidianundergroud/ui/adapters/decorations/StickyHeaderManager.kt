package com.ubadahj.qidianundergroud.ui.adapters.decorations

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.utils.setListener
import com.ubadahj.qidianundergroud.utils.ui.visible

interface StickyViewHolder<T> {
    val type: Int
    val root: View
    fun bind(item: T)
}

class StickyHeaderManager<T>(
    private val recyclerView: RecyclerView,
    private val adapter: ListAdapter<T, *>,
    private val header: StickyViewHolder<T>
) {

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            bind()
        }
    }

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            if (ViewCompat.isLaidOut(header.root)) bind()
            else header.root.post { bind() }
        }
    }

    private var hidden: Boolean = false

    init {
        recyclerView.addOnScrollListener(scrollListener)
        adapter.registerAdapterDataObserver(dataObserver)
    }

    fun toggleVisibility() {
        if (hidden) {
            hidden = (!hidden).updateVisibility()
            header.root.animate()
                .alpha(1f)
                .setListener(null)
                .start()
        } else {
            header.root.animate()
                .alpha(0f)
                .setListener { hidden = (!hidden).updateVisibility() }
                .start()
        }
    }

    private fun bind() {
        val listEmpty = adapter.currentList.isEmpty().updateVisibility()
        if (listEmpty) return

        val first = recyclerView.getOrNull(0) ?: return
        val firstPos = recyclerView.getChildAdapterPosition(first)
        if (!isValidPosition(firstPos)) return
        val atFirst = (firstPos == 0 && first.top == recyclerView.top).updateVisibility()
        if (atFirst) return

        val item: T?
        if (isHeader(firstPos)) {
            item = adapter.currentList[firstPos]
            header.root.translationY = 0f
        } else {
            item = findNearestHeader(firstPos)
            val secondPos = firstPos + 1
            if (isValidPosition(secondPos)) {
                if (isHeader(secondPos)) recyclerView.getOrNull(1)?.let { translateView(it) }
                else header.root.translationY = 0f
            }
        }

        item?.let { header.bind(it) }
        hidden.updateVisibility()
    }

    private fun translateView(view: View) {
        header.root.translationY = when (view.top <= header.root.bottom) {
            true -> (view.top - header.root.height).toFloat()
            false -> 0f
        }
    }

    private fun RecyclerView.getOrNull(position: Int): View? = try {
        get(position)
    } catch (e: Exception) {
        null
    }

    private fun findNearestHeader(position: Int): T? {
        for (i in position downTo 0) {
            if (isHeader(i)) return adapter.currentList[i]
        }
        return null
    }

    private fun isHeader(position: Int) = adapter.getItemViewType(position) == header.type

    private fun Boolean.updateVisibility() = also { header.root.visible = !it }

    private fun isValidPosition(position: Int): Boolean {
        return !(position == RecyclerView.NO_POSITION || position >= adapter.currentList.size)
    }
}