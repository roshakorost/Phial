package com.mindcoders.phial.internal;

import android.content.Context;

import java.io.File;

/**
 * Created by rost on 10/23/17.
 */

class InternalPhialConfig {
    //should match value in res/phial_share_paths.xml
    private static final String PHIAL_DATA = "PhialData";
    //should match authority in Manifest
    static final String PHIAL_AUTHORITY = "com.mindcoders.phial.fileprovider";

    private static final String SCREEN_SHOT_FILE_NAME = "screenshot.jpg";
    private static final String KEY_VALUE_FILE_NAME = "keyValues.json";
    static final int DEFAULT_SHARE_IMAGE_QUALITY = 95;
    static final String PREFERENCES_FILE_NAME = "phial";


    static File getWorkingDirectory(Context context) {
        final File file = new File(context.getExternalCacheDir(), PHIAL_DATA);
        file.mkdirs();
        return file;
    }

    static File getScreenShotFile(Context context) {
        final File directory = getWorkingDirectory(context);
        return new File(directory, SCREEN_SHOT_FILE_NAME);
    }

    static File getKeyValueFile(Context context) {
        final File directory = getWorkingDirectory(context);
        return new File(directory, KEY_VALUE_FILE_NAME);
    }
}
