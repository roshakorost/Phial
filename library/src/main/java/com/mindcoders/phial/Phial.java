package com.mindcoders.phial;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mindcoders.phial.keyvalue.KVEntry;
import com.mindcoders.phial.keyvalue.KVSaver;

import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public final class Phial {
    private static final KVStore KV_STORE = new KVStore();

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be set under default category.
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    public static void setKey(@NonNull String key, @Nullable String value) {
        KV_STORE.setKey(key, value);
    }

    /**
     * Removes entry with given key from debug data.
     * <p>
     * Note: Entry would be removed only from default category.
     *
     * @param key to be removed.
     */
    public static void removeKey(String key) {
        KV_STORE.removeKey(key);
    }

    /**
     * Set's category name that will be associated with Key.
     */
    public KVSaver category(String categoryName) {
        return KV_STORE.category(categoryName);
    }

    static List<KVEntry> getEntries() {
        return KV_STORE.getEntries();
    }
}
