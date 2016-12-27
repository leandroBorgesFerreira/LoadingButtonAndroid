package br.com.simplepass.loading_button_lib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by hinovamobile on 27/12/16.
 */

public class UtilsJava {
    public static Drawable getDrawable(Context context, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }

    }
}
