package com.mindcoders.phial.internal;

import android.content.Context;

import java.io.File;

/**
 * Created by rost on 10/23/17.
 */

class InternalPhialConfig {
    //should match value in res/path.xml
    private static final String PHIAL_DATA = "PhialData";
    private static final String SCREEN_SHOT_FILE_NAME = "screenshot.jpg";
    static final int DEFAULT_SHARE_IMAGE_QUALITY = 95;
    static final String SYSTEM_INFO_CATEGORY = "System";

    static File getShareFromDirectory(Context context) {
        final File file = new File(context.getExternalCacheDir(), PHIAL_DATA);
        file.mkdirs();
        return file;
    }

    static File getScreenShotFile(Context context) {
        final File directory = getShareFromDirectory(context);
        return new File(directory, SCREEN_SHOT_FILE_NAME);
    }
}
