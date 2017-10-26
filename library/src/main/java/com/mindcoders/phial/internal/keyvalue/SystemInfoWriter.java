package com.mindcoders.phial.internal.keyvalue;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.mindcoders.phial.BuildConfig;
import com.mindcoders.phial.keyvalue.Category;

import static android.os.Build.MANUFACTURER;


/**
 * Created by rost on 10/22/17.
 */

public final class SystemInfoWriter {
    private SystemInfoWriter() {
        //to hide
    }

    public static void writeSystemInfo(Category saver, Context context) {
        saver.setKey("Package", context.getPackageName());
        saver.setKey("Version", BuildConfig.VERSION_NAME);
        saver.setKey("Build Type", BuildConfig.BUILD_TYPE);
        saver.setKey("Board", Build.BOARD);
        saver.setKey("Brand", Build.BRAND);
        saver.setKey("Device", Build.DEVICE);
        saver.setKey("Model", Build.MODEL);
        saver.setKey("Manufacturer", MANUFACTURER);
        saver.setKey("Product", Build.PRODUCT);
        saver.setKey("SDK", Build.VERSION.SDK_INT);
        saver.setKey("OS Version", System.getProperty("os.version") + " (" + Build.VERSION.INCREMENTAL + ")");
        saver.setKey("SDCard state", Environment.getExternalStorageState());

        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        saver.setKey("Density", displayMetrics.density);
        saver.setKey("Width", displayMetrics.widthPixels);
        saver.setKey("Height", displayMetrics.heightPixels);
    }

}
