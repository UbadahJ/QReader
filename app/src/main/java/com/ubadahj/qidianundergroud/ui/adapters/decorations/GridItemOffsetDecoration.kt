package com.ubadahj.qidianundergroud.ui.adapters.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridItemOffsetDecoration(
    private val spanCount: Int,
    private val itemOffset: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        when {
            position < spanCount -> {
                // left grid
                if (position % 2 == 0)
                    rect.set(0, itemOffset, itemOffset / 2, itemOffset / 2)
                // right grid
                else
                    rect.set(itemOffset / 2, itemOffset, 0, itemOffset / 2)
            }
            // left grid
            position % 2 == 0 ->
                rect.set(0, itemOffset / 2, itemOffset / 2, itemOffset / 2)
            // right grid
            position % 2 == 1 ->
                rect.set(itemOffset / 2, itemOffset / 2, 0, itemOffset / 2)
            else -> {
                // left grid
                if (position % 2 == 0) rect.set(0, itemOffset / 2, itemOffset, itemOffset)
                // right grid
                else rect.set(itemOffset / 2, itemOffset / 2, 0, itemOffset)
            }
        }
    }

}