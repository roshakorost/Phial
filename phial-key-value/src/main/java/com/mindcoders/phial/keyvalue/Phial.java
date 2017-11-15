package com.mindcoders.phial.keyvalue;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Facade for associating keys with values.
 * e.g. Phial.setKey("applicationName", "some app") will create new key value association
 * second calling with same key will update it Phial.setKey("applicationName", "new app")
 * <p>
 * by default Phial.setKey(), Phial.removeKey() doesn't do any operation these calls are delegated to
 * list of savers {@link Saver} and {@link #addSaver(Saver)}
 * <p>
 * Phial overlay automatically adds it's saver to Phial so key values can be captured to share attachment
 * or can be viewed in debug section of Phial
 */
public class Phial {
    private static final List<Saver> SAVERS = new CopyOnWriteArrayList<>();
    private static final String DEFAULT_CATEGORY_NAME = "Default";
    private static final Category DEFAULT_CATEGORY = new Category(DEFAULT_CATEGORY_NAME, SAVERS);
    private static final ConcurrentMap<String, Category> CATEGORIES = new ConcurrentHashMap<>();

    static {
        CATEGORIES.put(DEFAULT_CATEGORY_NAME, DEFAULT_CATEGORY);
    }

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be set under default category.
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    public static void setKey(String key, String value) {
        DEFAULT_CATEGORY.setKey(key, value);
    }

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be set under default category.
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    public static void setKey(@NonNull String key, @Nullable Object value) {
        DEFAULT_CATEGORY.setKey(key, value);
    }


    /**
     * Removes entry with given key from debug data.
     * <p>
     * Note: Entry would be removed only from default category.
     *
     * @param key to be removed.
     */
    public static void removeKey(String key) {
        DEFAULT_CATEGORY.removeKey(key);
    }

    /**
     * Creates category that can be used for adding key values
     */
    public static Category category(String name) {
        CATEGORIES.putIfAbsent(name, new Category(name, SAVERS));
        return CATEGORIES.get(name);
    }

    /**
     * Removes category and associated keys.
     */
    public static void removeCategory(String name) {
        Category category = CATEGORIES.remove(name);
        category.clear();
    }

    /**
     * Add a new saver.
     */
    public static void addSaver(Saver saver) {
        SAVERS.add(saver);
    }


    /**
     * Remove previously added saver
     */
    public static void removeSaver(Saver saver) {
        SAVERS.remove(saver);
    }

    @VisibleForTesting
    static void cleanSavers() {
        SAVERS.clear();
    }
}
