package com.mindcoders.phial.internal.util;

import android.animation.Animator;
import android.support.annotation.NonNull;

public class SimpleAnimatorListener implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

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
