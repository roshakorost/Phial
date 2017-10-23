package com.mindcoders.phial.internal.keyvalue;

/**
 * Created by rost on 10/22/17.
 */

class KVEntry {
    private final String name;
    private final String value;

    KVEntry(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
