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
            throw new IllegalStateException(message);
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
}
