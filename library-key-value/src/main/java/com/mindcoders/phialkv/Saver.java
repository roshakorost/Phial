package com.mindcoders.phialkv;

/**
 * Created by rost on 10/24/17.
 */
public interface Saver {
    void save(String category, String key, String value);

    void remove(String category, String key);
}
