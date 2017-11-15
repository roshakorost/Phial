package com.mindcoders.phial.internal.util;

/**
 * Created by rost on 11/13/17.
 */

public final class ViewUtil {
    private ViewUtil() {
        //to hide
    }

    public static float distance(float x1, float y1, float x2, float y2) {
        final float dx = x1 - x2;
        final float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
