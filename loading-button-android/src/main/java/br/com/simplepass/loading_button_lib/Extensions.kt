package br.com.simplepass.loading_button_lib

import android.animation.Animator

fun Animator.disposeAnimator() {
    end()
    removeAllListeners()
    cancel()
}
