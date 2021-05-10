package com.ubadahj.qidianundergroud.utils.ui

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

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

val ViewGroup.inflater: LayoutInflater
    get() = LayoutInflater.from(context)

fun Number.toDp(context: Context): Number =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    )

fun View.snackBar(text: String, length: Int = Snackbar.LENGTH_SHORT) =
    Snackbar.make(this, text, length).show()