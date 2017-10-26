package com.mindcoders.phial.internal.util;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import java.io.IOException;

/**
 * Created by rost on 10/23/17.
 */

public final class ScreenShotUtil {
    private ScreenShotUtil() {
        //to hide
    }

    @NonNull
    public static Bitmap takeScreenShot(Window window) throws IOException {
        final View rootView = window.getDecorView().getRootView();
        final boolean drawingCacheEnabled = rootView.isDrawingCacheEnabled();
        rootView.setDrawingCacheEnabled(true);

        try {
            return Bitmap.createBitmap(rootView.getDrawingCache());
        } finally {
            rootView.setDrawingCacheEnabled(drawingCacheEnabled);
        }
    }
}
