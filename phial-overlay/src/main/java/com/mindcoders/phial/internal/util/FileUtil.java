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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        FileWriter fw = null;
        try {
            fw = new FileWriter(target);
            bw = new BufferedWriter(fw);
            bw.write(text);
        } finally {
            if (bw != null) {
                bw.close();
            }
            if (fw != null) {
                fw.close();
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
            final Set<String> takenNames = new HashSet<>();
            final List<File> files = walk(sources);

            for (File file : files) {
                final String uniqueName = uniqueName(file, takenNames);
                zipSingleFile(file, uniqueName, out);
                takenNames.add(uniqueName);
            }

        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static void zipSingleFile(File source, String nameInZip, ZipOutputStream out) throws IOException {
        out.putNextEntry(new ZipEntry(nameInZip));

        final byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(source), BUFFER_SIZE);

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


    public static List<File> walk(File source) {
        if (!source.isDirectory()) {
            return Collections.singletonList(source);
        }

        final File[] files = source.listFiles();
        return walk(Arrays.asList(files));
    }

    public static List<File> walk(List<File> sources) {
        final List<File> result = new ArrayList<>();
        for (File source : sources) {
            result.addAll(walk(source));
        }
        return result;
    }

    public static String uniqueName(File source, Set<String> taken) {
        String name = source.getName();
        for (int i = 1; taken.contains(name); i++) {
            name = getFileNameWithoutExtention(source) + "_" + i;
            final String extension = getFileExtension(source);
            if (!extension.isEmpty()) {
                name += "." + extension;
            }
        }
        return name;
    }

    public static String getFileExtension(File file) {
        final String fileName = file.getName();
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos > 0) {
            return fileName.substring(dotPos + 1);
        }
        return "";
    }

    public static String getFileNameWithoutExtention(File file) {
        final String fileName = file.getName();
        int dotPos = fileName.lastIndexOf('.');
        if (dotPos > 0) {
            return fileName.substring(0, dotPos);
        }
        return fileName;
    }
}
