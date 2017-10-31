package com.mindcoders.phial.internal.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UiUtils {

    private UiUtils() {
        // no instances
    }

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }


    public static int[] getRelativePosition(View view, View relatedTo) {
        final int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        final int[] relatedLocation = new int[2];
        relatedTo.getLocationOnScreen(relatedLocation);
        return new int[]{relatedLocation[0] - viewLocation[0], relatedLocation[1] - viewLocation[1]};
    }

    public static void hideKeyBoard(View focusedChild) {
        if (focusedChild != null) {
            InputMethodManager imm = (InputMethodManager) focusedChild.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusedChild.getWindowToken(), 0);
        }
    }
}
