package com.mindcoders.phial.internal.util.support;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;

/**
 * Created by rost on 11/1/17.
 */

public final class ContextCompat {
    private ContextCompat() {
        //to hide
    }

    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        }

        return context.getResources().getDrawable(id);
    }
}
