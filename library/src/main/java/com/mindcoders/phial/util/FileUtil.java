package com.mindcoders.phial.util;

import java.io.BufferedWriter;
import java.io.File;
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
}
