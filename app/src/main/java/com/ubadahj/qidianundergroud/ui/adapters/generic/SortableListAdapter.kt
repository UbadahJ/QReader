package com.ubadahj.qidianundergroud.ui.adapters.generic

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class SortableListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : FilterableListAdapter<T, VH>(diffCallback) {

    private var reversed = false
    private var originalList: List<T>? = listOf()
    protected abstract var sortedBy: (T) -> Comparable<*>?

    fun sortBy(selector: (T) -> Comparable<*>? = sortedBy, reverse: Boolean = false) {
        sortedBy = selector
        reversed = reverse
        if (originalList.isNullOrEmpty()) return
        submitList(originalList)
    }

    override fun submitList(list: List<T>?) {
        originalList = list
        val sorted = list?.sortedWith(compareBy(sortedBy))
            .let {
                if (reversed) it?.reversed()
                else it
            }

        super.submitList(sorted)
    }

}