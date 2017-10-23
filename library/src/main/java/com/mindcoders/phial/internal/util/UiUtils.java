package com.mindcoders.phial.internal.util;

import android.content.Context;
import android.util.DisplayMetrics;

public class UiUtils {

    private UiUtils() {
        // no instances
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
