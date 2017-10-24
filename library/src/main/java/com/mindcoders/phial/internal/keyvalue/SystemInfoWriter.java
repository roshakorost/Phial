package com.mindcoders.phial.internal.keyvalue;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

import com.mindcoders.phial.BuildConfig;
import com.mindcoders.phialkv.Category;

/**
 * Created by rost on 10/22/17.
 */

public final class SystemInfoWriter {
    private SystemInfoWriter() {
        //to hide
    }

    public static void writeSystemInfo(Category saver, Context context) {
        saver.setKey("Version", BuildConfig.VERSION_NAME);
        saver.setKey("Build Type", BuildConfig.BUILD_TYPE);
        saver.setKey("Board", Build.BOARD);
        saver.setKey("Brand", Build.BRAND);
        saver.setKey("Device", Build.DEVICE);
        saver.setKey("Model", Build.MODEL);
        saver.setKey("Product", Build.PRODUCT);
        saver.setKey("Display", Build.DISPLAY);
        saver.setKey("SDK", Build.VERSION.SDK_INT);

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        saver.setKey("Density", displayMetrics.density);
        saver.setKey("Width", displayMetrics.widthPixels);
        saver.setKey("Height", displayMetrics.heightPixels);
    }

}
