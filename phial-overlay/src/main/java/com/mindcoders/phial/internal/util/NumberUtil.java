package com.mindcoders.phial.internal.util;

/**
 * Created by rost on 11/21/17.
 */

public final class NumberUtil {
    private NumberUtil() {
        //to hide
    }

    public static float clipTo(float value, float min, float max) {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }
}
