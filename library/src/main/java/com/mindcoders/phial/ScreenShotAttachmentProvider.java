package com.mindcoders.phial;

import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by rost on 10/22/17.
 */

public class ScreenShotAttachmentProvider implements AttachmentProvider {
    private final Window window;
    private final File targetFile;
    private final int quality;

    public ScreenShotAttachmentProvider(Window window, File targetFile, int quality) {
        this.window = window;
        this.targetFile = targetFile;
        this.quality = quality;
    }


    @Override
    public File provideAttachment() throws Exception {
        final View rootView = window.getDecorView().getRootView();
        final boolean drawingCacheEnabled = rootView.isDrawingCacheEnabled();
        rootView.setDrawingCacheEnabled(true);
        final Bitmap bitmap;
        try {
            bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        } finally {
            rootView.setDrawingCacheEnabled(drawingCacheEnabled);
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(targetFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

        return targetFile;
    }
}
