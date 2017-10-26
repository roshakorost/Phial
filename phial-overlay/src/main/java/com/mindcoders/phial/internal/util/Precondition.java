package com.mindcoders.phial.internal.util;

import android.view.View;

/**
 * Created by rost on 10/23/17.
 */

public final class Precondition {
    private Precondition() {
        //to hide-
    }

    public static <T> T notNull(T item, String message) {
        if (item == null) {
            throw new IllegalStateException(message);
        }

        return item;
    }

    public static void calledFromTools(View view) {
        if (!view.isInEditMode()) {
            throw new IllegalArgumentException("should be called only from AndroidStudioTools");
        }
    }
}
