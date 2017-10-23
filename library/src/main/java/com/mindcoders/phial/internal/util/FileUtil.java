package com.mindcoders.phial.internal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by rost on 10/22/17.
 */

public final class FileUtil {
    private FileUtil() {
    }

    public static void write(String text, File target) throws IOException {
        BufferedWriter bw = null;
        try {
            final FileWriter fw = new FileWriter(target);
            bw = new BufferedWriter(fw);
            bw.write(text);
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(context, "com.wsitrader.fileprovider", file);
        } else {
            return Uri.fromFile(file);
        }
    }

    public static void saveBitmap(Bitmap bitmap, File targetFile, int quality) throws IOException {
        saveBitmap(bitmap, targetFile, Bitmap.CompressFormat.JPEG, quality);
    }

    public static void saveBitmap(Bitmap bitmap, File targetFile, Bitmap.CompressFormat format, int quality) throws IOException {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(targetFile);
            bitmap.compress(format, quality, outputStream);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
