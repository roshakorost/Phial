package com.mindcoders.phial;

import com.mindcoders.phial.Phial;

/**
 * Created by rost on 10/22/17.
 */

public interface KVSaver {
    /**
     * Sets a value to be associated with your debug data.
     * Entry will be under the category set via {@link Phial#category(String)}
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    void setKey(String key, String value);

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be under the category set via {@link Phial#category(String)}
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    void setKey(String key, Object value);

    /**
     * Removes entry with given key from debug data.
     * <p>
     * Note: Entry would be removed only from the category set via {@link Phial#category(String)}
     *
     * @param key to be removed.
     */
    void removeKey(String key);
}
