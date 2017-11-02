package com.mindcoders.phial.keyvalue;

/**
 * Implementations should store key values
 */
public interface Saver {
    /**
     * Should save key value association
     *
     * @param category - category provided by {@link Phial#category(String)}
     * @param key      - provided key
     * @param value    - provided value
     */
    void save(String category, String key, String value);

    /**
     * Should delete key value association
     *
     * @param category - category provided by {@link Phial#category(String)}
     * @param key      - provided key
     */
    void remove(String category, String key);
}
