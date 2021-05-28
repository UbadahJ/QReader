package com.ubadahj.qidianundergroud.utils

import android.animation.Animator
import android.view.ViewPropertyAnimator

fun ViewPropertyAnimator.setListener(listener: (Animator?) -> Unit): ViewPropertyAnimator {
    return this.setListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            listener(animation)
        }
    })
}
