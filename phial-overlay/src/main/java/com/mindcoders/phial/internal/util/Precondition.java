package com.mindcoders.phial.internal.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by rost on 10/23/17.
 */

public final class Precondition {
    private Precondition() {
        //to hide-
    }

    public static <T> T notNull(T item, String message) {
        if (item == null) {
            throw new IllegalArgumentException(message);
        }

        return item;
    }

    public static void calledFromTools(View view) {
        if (!view.isInEditMode()) {
            throw new IllegalArgumentException("should be called only from AndroidStudioTools");
        }
    }

    public static void notImplemented(String what, Context context) {
        Toast.makeText(context, what + " is NOT Implemented yet", Toast.LENGTH_SHORT).show();
    }

    public static void isTrue(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void notEmpty(T[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(int[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
