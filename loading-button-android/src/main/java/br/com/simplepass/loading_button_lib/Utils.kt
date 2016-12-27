package br.com.simplepass.loading_button_lib

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION
import android.os.Build



/**
 * Created by hinovamobile on 27/12/16.
 */
class Utils {

    companion object {
        fun getColorWrapper(context: Context, id: Int) : Int {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return context.getColor(id)
            } else {
                return context.getResources().getColor(id)
            }
        }
    }

    fun getDrawable(context: Context, id: Int) : Drawable{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id)
        } else {
            return context.getResources().getDrawable(id)
        }

    }

}