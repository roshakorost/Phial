package com.mindcoders.phial.internal.util;

import android.animation.Animator;
import android.support.annotation.NonNull;

/**
 * Provides default(empty) implementation of Animator.AnimatorListener
 */
public class SimpleAnimatorListener implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {
        //optional
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //optional
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        //optional
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        //optional
    }

    public static Animator.AnimatorListener createEndListener(@NonNull Runnable runnable) {
        return new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                runnable.run();
            }
        };
    }

}
