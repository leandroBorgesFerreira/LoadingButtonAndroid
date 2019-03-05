package br.com.simplepass.loadingbutton.utils

import android.graphics.drawable.*
import android.os.Build

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
        else -> throw RuntimeException("Error reading background... Use a shape or a color in xml!")
    }
