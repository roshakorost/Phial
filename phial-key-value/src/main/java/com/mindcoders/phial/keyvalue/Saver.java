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


    /**
     * Delete the category and all the key-value pairs associated with it.
     * @param category - name of the category to be deleted
     */
    void remove(String category);
}
