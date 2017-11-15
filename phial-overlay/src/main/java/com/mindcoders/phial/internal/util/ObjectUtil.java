package com.mindcoders.phial.internal.util;

/**
 * Created by rost on 11/3/17.
 */

public final class ObjectUtil {
    private ObjectUtil() {
        //to hide
    }

    public static <T> boolean equals(T a, T b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
