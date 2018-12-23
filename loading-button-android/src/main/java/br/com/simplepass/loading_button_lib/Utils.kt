package br.com.simplepass.loading_button_lib

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build

class Utils {
    companion object {
        fun getColorWrapper(context: Context, id: Int) : Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.getColor(id)
            } else {
                context.resources.getColor(id)
            }
    }

    fun getDrawable(context: Context, id: Int) : Drawable? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.getDrawable(id)
        } else {
            context.resources.getDrawable(id)
        }
}
