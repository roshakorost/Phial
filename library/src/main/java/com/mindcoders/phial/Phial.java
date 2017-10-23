package com.mindcoders.phial;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mindcoders.phial.internal.PhialCore;
import com.mindcoders.phial.internal.keyvalue.KVCategoryProvider;
import com.mindcoders.phial.internal.util.Precondition;

/**
 * Created by rost on 10/22/17.
 */

public final class Phial {
    private static KVCategoryProvider categoryProvider;


    public static PhialBuilder builder(Application application) {
        return new PhialBuilder(application);
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
    public static void setKey(@NonNull String key, @Nullable String value) {
        shouldBeInited(categoryProvider)
                .defaultCategory().setKey(key, value);
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
        shouldBeInited(categoryProvider)
                .defaultCategory().setKey(key, value);
    }

    /**
     * Removes entry with given key from debug data.
     * <p>
     * Note: Entry would be removed only from default category.
     *
     * @param key to be removed.
     */
    public static void removeKey(String key) {
        shouldBeInited(categoryProvider)
                .defaultCategory().removeKey(key);
    }

    /**
     * Set's category name that will be associated with Key.
     */
    public static KVSaver category(String categoryName) {
        return shouldBeInited(categoryProvider)
                .category(categoryName);
    }

    private static <T> T shouldBeInited(T item) {
        return Precondition.notNull(item, "PhialBuilder.init should be called before using Phial");
    }

    static void init(PhialCore phialCore) {
        categoryProvider = phialCore.getCategoryProvider();
    }

}
