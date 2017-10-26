package com.mindcoders.phial.keyvalue;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by rost on 10/24/17.
 */
public class Category {
    private final String name;
    private final List<Saver> savers;

    Category(String name, List<Saver> savers) {
        this.name = name;
        this.savers = savers;
    }

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be under the category set via {@link Phial#category(String)}
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    public Category setKey(@NonNull String key, String value) {
        verifyKeyIsNotNull(key);
        for (Saver saver : savers) {
            saver.save(name, key, value);
        }
        return this;
    }

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be under the category set via {@link Phial#category(String)}
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    public Category setKey(@NonNull String key, Object value) {
        verifyKeyIsNotNull(key);
        for (Saver saver : savers) {
            saver.save(name, key, String.valueOf(value));
        }
        return this;
    }

    /**
     * Removes entry with given key from debug data.
     * <p>
     * Note: Entry would be removed only from the category set via {@link Phial#category(String)}
     *
     * @param key to be removed.
     */
    public Category removeKey(@NonNull String key) {
        verifyKeyIsNotNull(key);
        for (Saver saver : savers) {
            saver.remove(name, key);
        }
        return this;
    }

    private void verifyKeyIsNotNull(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null.");
        }
    }
}
