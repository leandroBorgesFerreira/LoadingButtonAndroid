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
    this.changeLayoutProperty { this.width = width }
}

internal fun View.updateHeight(height: Int) {
    this.changeLayoutProperty { this.height = height }
}

private fun View.changeLayoutProperty(func: ViewGroup.LayoutParams.() -> Unit) {
    this.layoutParams = this.layoutParams.apply(func)
}
