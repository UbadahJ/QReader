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
        val halfOffset = itemOffset / 2
        when {
            // Top Row
            position < spanCount -> {
                when {
                    // left grid
                    position % spanCount == 0 -> rect.set(0, itemOffset, halfOffset, halfOffset)
                    // right grid
                    position % spanCount == spanCount - 1 -> rect.set(
                        halfOffset,
                        itemOffset,
                        0,
                        halfOffset
                    )
                    // middle
                    else -> rect.set(halfOffset, itemOffset, halfOffset, halfOffset)
                }
            }
            // left grid
            position % spanCount == 0 -> rect.set(0, halfOffset, halfOffset, halfOffset)
            // middle
            position % spanCount in (1 until spanCount) -> rect.set(
                halfOffset,
                halfOffset,
                halfOffset,
                halfOffset
            )
            // right grid
            position % spanCount == spanCount - 1 -> rect.set(halfOffset, halfOffset, 0, halfOffset)
            // Bottom Row
            else -> {
                when {
                    // left grid
                    position % spanCount == 0 -> rect.set(0, halfOffset, itemOffset, itemOffset)
                    // right grid
                    position % spanCount == spanCount - 1 -> rect.set(
                        halfOffset,
                        halfOffset,
                        0,
                        itemOffset
                    )
                    else -> rect.set(halfOffset, halfOffset, itemOffset, itemOffset)
                }
            }
        }
    }

}
