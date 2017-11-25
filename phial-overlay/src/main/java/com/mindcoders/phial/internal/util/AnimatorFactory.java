package com.mindcoders.phial.internal.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by rost on 10/29/17.
 */

public abstract class AnimatorFactory {
    final int x;
    final int y;
    final int startRadius;

    AnimatorFactory(View startView) {
        final int width = startView.getWidth();
        final int height = startView.getHeight();
        startRadius = Math.min(width, height) / 2;

        int[] location = new int[2];
        startView.getLocationOnScreen(location);
        x = location[0] + width / 2;
        y = location[1] + height / 2;
    }

    public static AnimatorFactory createFactory(View startView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RevealAnimatorFactory(startView);
        } else {
            return new FadeAnimatorFactory(startView);
        }
    }

    public abstract Animator createAppearAnimator(View targetView);

    public abstract Animator createDisappearAnimator(View targetView);

    static int calcRadius(int width, int height) {
        return (int) Math.round(Math.sqrt((float) width * width + height * height));
    }
}

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class RevealAnimatorFactory extends AnimatorFactory {
    RevealAnimatorFactory(View startView) {
        super(startView);
    }

    private int[] getRelativeLocation(View targetView) {
        int[] location = new int[2];
        targetView.getLocationOnScreen(location);
        return new int[]{x - location[0], y - location[1]};
    }

    @Override
    public Animator createAppearAnimator(View targetView) {
        return createAnimator(targetView, true);
    }

    @Override
    public Animator createDisappearAnimator(View targetView) {
        return createAnimator(targetView, false);
    }

    private Animator createAnimator(View targetView, boolean isAppearing) {
        final int[] relativeLocation = getRelativeLocation(targetView);
        final int width = targetView.getWidth();
        final int height = targetView.getHeight();

        final int relX = relativeLocation[0];
        final int relY = relativeLocation[1];
        int dx = Math.max(relX, width - relX);
        int dy = Math.max(relY, height - relY);
        int radius = calcRadius(dx, dy);

        return ViewAnimationUtils.createCircularReveal(
                targetView,
                relX,
                relY,
                isAppearing ? startRadius : radius,
                isAppearing ? radius : startRadius
        );
    }
}

class FadeAnimatorFactory extends AnimatorFactory {
    FadeAnimatorFactory(View startView) {
        super(startView);
    }

    @Override
    public Animator createAppearAnimator(View targetView) {
        return ObjectAnimator.ofFloat(targetView, "alpha", 0f, 1f);
    }

    @Override
    public Animator createDisappearAnimator(View targetView) {
        return ObjectAnimator.ofFloat(targetView, "alpha", 1f, 0f);
    }
}
