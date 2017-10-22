package com.mindcoders.phial;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;

import com.mindcoders.phial.keyvalue.KVSaver;

/**
 * Created by rost on 10/22/17.
 */

final class SystemInfoWriter {
    private SystemInfoWriter() {
        //to hide
    }

    static void writeSystemInfo(KVSaver saver, Context context) {
        saver.setKey("version", BuildConfig.VERSION_NAME);
        saver.setKey("build type ", BuildConfig.BUILD_TYPE);
        saver.setKey("Board", Build.BOARD);
        saver.setKey("Brand", Build.BRAND);
        saver.setKey("Device", Build.DEVICE);
        saver.setKey("Model", Build.MODEL);
        saver.setKey("Product", Build.PRODUCT);
        saver.setKey("Display", Build.DISPLAY);
        saver.setKey("SDK", Build.VERSION.SDK_INT);

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        saver.setKey("density", displayMetrics.density);
        saver.setKey("width", displayMetrics.widthPixels);
        saver.setKey("height", displayMetrics.heightPixels);
    }

}
