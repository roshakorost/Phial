package com.mindcoders.phialkv;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 10/24/17.
 */

public class Phial {
    private static final List<Saver> SAVERS = new CopyOnWriteArrayList<>();
    private static final String DEFAULT_CATEGORY_NAME = "default";
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
     * Set's category name that will be associated with Key.
     */
    public static Category category(String name) {
        CATEGORIES.putIfAbsent(name, new Category(name, SAVERS));
        return CATEGORIES.get(name);
    }

    public static void addSaver(Saver saver) {
        SAVERS.add(saver);
    }

    public static void removeSaver(Saver saver) {
        SAVERS.remove(saver);
    }
}
