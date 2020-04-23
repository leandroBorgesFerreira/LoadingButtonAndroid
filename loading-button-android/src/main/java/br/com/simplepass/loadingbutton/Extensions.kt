package br.com.simplepass.loadingbutton

import android.animation.Animator
import android.view.View
import android.view.ViewGroup

internal fun Animator.disposeAnimator() {
    end()
    removeAllListeners()
    cancel()
}

internal fun View.updateWidth(width: Int) {
    this.changeProperty { this.width = width }
}

internal fun View.updateHeight(height: Int) {
    this.changeProperty { this.height = height }
}

private fun View.changeProperty(func: ViewGroup.LayoutParams.() -> Unit) {
    this.layoutParams = this.layoutParams.apply(func)
}
