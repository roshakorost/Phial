package com.mindcoders.phial.internal.util.support;

import android.os.Build;
import android.view.View;

/**
 * Created by rost on 11/1/17.
 */

public final class ViewCompat {
    private ViewCompat() {
        //to hide
    }

    public static void setElevation(View view, float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setElevation(elevation);
        }
    }
}
