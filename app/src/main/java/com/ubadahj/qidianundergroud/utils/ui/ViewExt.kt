package com.ubadahj.qidianundergroud.utils.ui

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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

fun Spinner.onItemSelectedListener(onChange: (position: Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        private var init = false
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            if (init) onChange(position)
            else init = true
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}

fun Activity.showSystemBar(enable: Boolean) {
    if (!enable) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.getWindowInsetsController(window.decorView)?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    } else {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        ViewCompat.getWindowInsetsController(window.decorView)
            ?.show(WindowInsetsCompat.Type.systemBars())
    }
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

fun View.snackBar(text: String, length: Int = Snackbar.LENGTH_SHORT) = try {
    Snackbar.make(this, text, length).show()
} catch (e: Exception) {
}

fun View.snackBar(@StringRes text: Int, length: Int = Snackbar.LENGTH_SHORT) = try {
    Snackbar.make(this, text, length).show()
} catch (e: Exception) {
}
