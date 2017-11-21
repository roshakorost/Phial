package com.mindcoders.phial.internal.util;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by rost on 11/21/17.
 */

public final class AnimationUtil {
    private static final long MOVE_TO_EDGE_DURATION = 150L;

    private AnimationUtil() {
        //to hide
    }

    public static void animateX(View view, float targetX) {
        view.animate()
                .setDuration(MOVE_TO_EDGE_DURATION)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .x(targetX)
                .start();
    }

}
