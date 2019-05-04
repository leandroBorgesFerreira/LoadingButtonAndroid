package br.com.simplepass.loadingbutton.utils

import android.content.Context
import android.graphics.drawable.*
import android.os.Build
import android.view.ContextThemeWrapper
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal fun parseGradientDrawable(drawable: Drawable): GradientDrawable =
    when (drawable) {
        is GradientDrawable -> drawable
        is ColorDrawable -> GradientDrawable().apply { setColor(drawable.color) }
        is InsetDrawable -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                drawable.drawable?.let { innerDrawable ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        when (innerDrawable) {
                            is RippleDrawable -> {
                                parseGradientDrawable(innerDrawable.getDrawable(0))
                            }
                            else -> parseGradientDrawable(innerDrawable)
                        }
                    } else {
                        parseGradientDrawable(innerDrawable)
                    }
                }
                    ?: throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            } else {
                throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            }
        }
        is StateListDrawable -> {
            if (drawable.current is GradientDrawable) {
                drawable.current as GradientDrawable
            } else {
                throw RuntimeException("Error reading background... Use a shape or a color in xml!")
            }
        }
        is LayerDrawable -> {
            parseGradientDrawable(drawable.getDrawable(0))
        }
        else -> throw RuntimeException("Error reading background... Use a shape or a color in xml!")
    }

internal fun Context.addLifecycleObserver(observer: LifecycleObserver) {
    when {
        this is LifecycleOwner ->
            (this as LifecycleOwner).lifecycle.addObserver(observer)
        this is ContextThemeWrapper ->
            (this.baseContext as LifecycleOwner).lifecycle.addObserver(observer)
        this is androidx.appcompat.view.ContextThemeWrapper ->
            (this.baseContext as LifecycleOwner).lifecycle.addObserver(observer)
    }
}