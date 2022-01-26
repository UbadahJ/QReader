package com.ubadahj.qidianundergroud.ui.listeners

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val SWIPE_DISTANCE_THRESHOLD = 160
private const val SWIPE_VELOCITY_THRESHOLD = 160

/**
 * Detects left and right swipes across a view.
 */
open class OnGestureListener(context: Context) : RecyclerView.OnItemTouchListener {
    private val gestureDetector: GestureDetector
    private val scaleDetector: ScaleGestureDetector

    init {
        gestureDetector = GestureDetector(context, SwipeListener())
        scaleDetector = ScaleGestureDetector(context, ScaleGestureListener())
    }

    open fun onSwipeLeft() {}
    open fun onSwipeRight() {}
    open fun onScaleView(scale: Float) {}

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(e) && scaleDetector.onTouchEvent(e)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    private inner class SwipeListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            e1 ?: return false
            e2 ?: return false
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (abs(distanceX) > abs(distanceY) &&
                abs(distanceX) > SWIPE_DISTANCE_THRESHOLD &&
                abs(velocityX) > SWIPE_VELOCITY_THRESHOLD
            ) {
                if (distanceX > 0) onSwipeRight() else onSwipeLeft()
                return true
            }
            return false
        }
    }

    private inner class ScaleGestureListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private var scaleFactor = 1f

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 10.0f))
            onScaleView(scaleFactor)
            return true
        }
    }

}