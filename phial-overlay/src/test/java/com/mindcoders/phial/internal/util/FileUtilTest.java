package com.mindcoders.phial.internal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * Created by rost on 11/13/17.
 */
@RunWith(RobolectricTestRunner.class)
public class FileUtilTest {
    private static final String FILE_NAME = "test";
    private static final String TEST_TEXT = "test";
    private File file;

    @Before
    public void setUp() throws Exception {
        file = new File(FILE_NAME);
    }

    @After
    public void tearDown() throws Exception {
        if (file.isDirectory()) {
            deleteDirectoryAndSubdirs(file);
        } else {
            file.delete();
        }
    }


    @Test
    public void write() throws Exception {
        FileUtil.write("test text", file);
        Assert.assertEquals(
                "test text",
                Files.lines(Paths.get(FILE_NAME)).reduce("", (l, r) -> l + r)
        );
    }

    @Test
    public void saveBitmap() throws Exception {
        final Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        FileUtil.saveBitmap(bitmap, file, 100);

        final Bitmap result = BitmapFactory.decodeFile(FILE_NAME);
        Assert.assertEquals(100, result.getHeight());
        Assert.assertEquals(100, result.getWidth());
    }

    @Test
    public void saveBitmapWithConfig() throws Exception {
        final Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
        FileUtil.saveBitmap(bitmap, file, Bitmap.CompressFormat.PNG, 100);

        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        final Bitmap result = BitmapFactory.decodeFile(FILE_NAME, opts);

        Assert.assertEquals(100, result.getHeight());
        Assert.assertEquals(100, result.getWidth());
        Assert.assertEquals(bitmap.getPixel(0, 0), result.getPixel(0, 0));
    }

    @Test
    public void walk() {
        final File dir1 = createDirectories("dir1");
        final File dir12 = createDirectories("dir1", "dir2");
        final File dir2 = createDirectories("dir2");
        final File dir3 = createDirectories("dir3");

        createFiles(dir1, "f1.txt", "f2.txt", "f3.txt");
        createFiles(dir12, "f1.txt", "f4.txt");
        createFiles(dir2);
        createFiles(dir3, "f1.txt");

        Assert.assertEquals(CollectionUtils.asSet(
                new File(dir1, "f1.txt"), new File(dir1, "f2.txt"), new File(dir1, "f3.txt"),
                new File(dir12, "f1.txt"), new File(dir12, "f4.txt"),
                new File(dir3, "f1.txt")),

                new HashSet<>(FileUtil.walk(file))
        );
    }

    @Test
    public void uniqueName_returns_same_if_not_taken() {
        final File file = new File("f.txt");
        Assert.assertEquals("f.txt", FileUtil.uniqueName(file, new HashSet<>()));
    }

    @Test
    public void uniqueName_returns_new_name_if_taken() {
        final File file = new File("f.txt");
        Assert.assertEquals("f_2.txt", FileUtil.uniqueName(file, CollectionUtils.asSet("f.txt", "f_1.txt")));
    }

    @Test
    public void uniqueName_returns_new_name_if_taken_file_without_extension() {
        final File file = new File("f");
        Assert.assertEquals("f_2", FileUtil.uniqueName(file, CollectionUtils.asSet("f", "f_1")));
    }

    @Test
    public void uniqueName_returns_new_name_if_taken_file_starts_with_dot() {
        final File file = new File(".ignore");
        Assert.assertEquals(".ignore_2", FileUtil.uniqueName(file, CollectionUtils.asSet(".ignore", ".ignore_1")));
    }


    @Test
    public void zip_single_file() throws Exception {
        file.mkdirs();

        final File target = new File(file, "target.zip");
        final File f1 = new File(file, "f1.txt");
        FileUtil.write(TEST_TEXT, f1);
        FileUtil.zip(Collections.singletonList(f1), target);

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(target))) {
            final ZipEntry entry = zipInputStream.getNextEntry();
            Assert.assertEquals("f1.txt", entry.getName());

            final Scanner scanner = new Scanner(zipInputStream);
            final String line = scanner.nextLine();
            Assert.assertEquals(TEST_TEXT, line);
        }

    }

    @Test
    public void zip_files_with_same_name() throws Exception {
        final File target = new File(file, "target.zip");

        final File dir1 = createDirectories("dir1");
        final File dir2 = createDirectories("dir2");

        final File f1 = new File(dir1, "f1");
        final File f2 = new File(dir2, "f1");
        FileUtil.write("test", f1);
        FileUtil.write("test", f2);

        FileUtil.zip(Arrays.asList(f1, f2), target);
    }


    @Test
    public void zip_all_files_in_directory() throws Exception {
        file.mkdirs();
        final File target = new File(file, "target.zip");

        final File dir1 = createDirectories("dir", "dir1");
        final File dir12 = createDirectories("dir", "dir1", "dir2");
        final File dir2 = createDirectories("dir", "dir2");
        final File dir3 = createDirectories("dir", "dir3");

        createFiles(dir1, "f1.txt", "f2.txt", "f3.txt");
        createFiles(dir12, "f1.txt", "f4.txt");
        createFiles(dir2);
        createFiles(dir3, "f1.txt");
        FileUtil.zip(Collections.singletonList(new File(file, "dir")), target);

        Set<String> names = new HashSet<>();
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(target))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                names.add(entry.getName());
            }
        }
        Assert.assertEquals(CollectionUtils.asSet("f1.txt", "f2.txt", "f3.txt", "f1_1.txt", "f4.txt", "f1_2.txt"), names);
    }


    private File createDirectories(String... dirNames) {
        File directory = file;
        for (String dirName : dirNames) {
            directory = new File(directory, dirName);
        }
        directory.mkdirs();
        return directory;
    }

    private void createFiles(File dir, String... files) {
        for (String name : files) {
            final File file = new File(dir, name);
            try {
                FileUtil.write(TEST_TEXT, file);
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
    }

    private void deleteDirectoryAndSubdirs(File file) throws IOException {
        Files.walk(Paths.get(file.getAbsolutePath()))
                .map(Path::toFile)
                .sorted(Comparator.reverseOrder())
                .forEach(File::delete);
    }
}