package com.mindcoders.phial.internal.util;

/**
 * Created by rost on 10/23/17.
 */

public final class Precondition {
    private Precondition(){
        //to hide-
    }
    public static <T> T notNull(T item, String message) {
        if (item == null) {
            throw new IllegalStateException(message);
        }

        return item;
    }
}
