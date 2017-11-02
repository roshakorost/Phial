package com.mindcoders.phial.sample;

import android.content.Context;
import android.support.annotation.Nullable;

import com.mindcoders.phial.Attacher;

import java.io.File;

class SharedPreferencesAttacher implements Attacher {
    private static final String SHARED_PREFS_DIR_NAME = "shared_prefs";
    private Context context;

    SharedPreferencesAttacher(Context context) {
        this.context = context;
    }

    @Override
    @Nullable
    public File provideAttachment() throws Exception {
        //if we can't find shared prefs just return null, so they will not be included.
        return getSharedPrefsDir(context);
    }

    @Override
    public void onPreDebugWindowCreated() {
        //  is called before debug window is show. You might wan't to capture screen shot
        //  or perform any other actions before debug window is show.
    }

    @Override
    public void onAttachmentNotNeeded() {
        // you might wan't to clean your created file after successful share if it is no longer needed.
        // E.g. in case provideAttachment dumps thread states and stores them in temporary file
        // it might clean the temporary file when onAttachmentNotNeeded is called.
    }

    @Nullable
    private static File getDataDir(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return context.getDataDir();
        }
        final File filesDir = context.getFilesDir();
        if (filesDir != null) {
            return filesDir.getParentFile();
        }

        return null;
    }

    @Nullable
    private static File getSharedPrefsDir(Context context) {
        final File dataDir = getDataDir(context);
        if (dataDir != null) {
            final File file = new File(dataDir, SHARED_PREFS_DIR_NAME);
            if (file.exists() && file.isDirectory()) {
                return file;
            }
        }

        // prefs are not in the default location. Where they could be?
        return null;
    }
}
