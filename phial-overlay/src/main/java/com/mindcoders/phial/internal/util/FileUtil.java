package com.mindcoders.phial.internal.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import com.mindcoders.phial.internal.util.support.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by rost on 10/22/17.
 */

public final class FileUtil {
    private static final int BUFFER_SIZE = 2048;

    private FileUtil() {
        //to hide
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

    public static Uri getUri(Context context, String authority, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return FileProvider.getUriForFile(context, authority, file);
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

    public static void zip(List<File> sources, File target) throws IOException {
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target)));
            for (File source : sources) {
                writeFile(source, out);
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static void writeFile(File source, ZipOutputStream out) throws IOException {
        if (source.isDirectory()) {
            final File[] files = source.listFiles();
            for (File file : files) {
                writeFile(file, out);
            }
            return;
        }

        final byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(source), BUFFER_SIZE);

            out.putNextEntry(new ZipEntry(source.getName()));

            int count;
            while ((count = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, count);
            }

        } finally {
            if (bis != null) {
                bis.close();
            }
        }
    }
}
