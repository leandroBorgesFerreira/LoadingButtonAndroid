package br.com.simplepass.loading_button_lib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class UtilsJava {
    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }
}
