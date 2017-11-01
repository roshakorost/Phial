package com.mindcoders.phial.internal.util.support;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by rost on 11/1/17.
 */

public final class ResourcesCompat {
    private ResourcesCompat() {
        //to hide
    }

    @ColorInt
    @SuppressWarnings("deprecation")
    public static int getColor(@NonNull Resources res, @ColorRes int id, @Nullable Resources.Theme theme)
            throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return res.getColor(id, theme);
        } else {
            return res.getColor(id);
        }
    }
}
