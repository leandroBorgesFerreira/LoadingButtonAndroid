package br.com.simplepass.loading_button_lib;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

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

    public static void setViewBackground(View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static Drawable[] getViewCompoundDrawables(TextView view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return view.getCompoundDrawablesRelative();
        else
            return view.getCompoundDrawables();
    }

    public static void setViewCompoundDrawables(@NonNull TextView view,
                                                @Nullable Drawable start, @Nullable Drawable top,
                                                @Nullable Drawable end, @Nullable Drawable bottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            view.setCompoundDrawablesRelative(start, top, end, bottom);
        } else {
            view.setCompoundDrawables(start, top, end, bottom);
        }
    }
}
