package br.com.simplepass.loading_button_lib

import android.animation.Animator

internal fun Animator.disposeAnimator() {
    end()
    removeAllListeners()
    cancel()
}
