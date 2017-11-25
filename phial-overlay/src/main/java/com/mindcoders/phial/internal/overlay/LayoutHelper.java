package com.mindcoders.phial.internal.overlay;

import android.view.View;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rost on 11/20/17.
 */

class LayoutHelper {
    interface ViewsDidLayoutListener {
        void onLayout();
    }

    interface Disposable {
        void dispose();

        Disposable EMPTY = () -> {
        };
    }

    static Disposable onLayout(ViewsDidLayoutListener listener, View... views) {
        final Set<View> unmeasured = new HashSet<>();
        for (View view : views) {
            if (!isViewMeasured(view)) {
                unmeasured.add(view);
            }
        }

        final MyLayoutListener layoutListener = new MyLayoutListener(listener, unmeasured);
        for (View target : unmeasured) {
            target.addOnLayoutChangeListener(layoutListener);
        }

        if (unmeasured.isEmpty()) {
            listener.onLayout();
        }

        return layoutListener;
    }

    private static class MyLayoutListener implements View.OnLayoutChangeListener, Disposable {
        private final ViewsDidLayoutListener listener;
        private final Set<View> targets;

        private MyLayoutListener(ViewsDidLayoutListener listener, Set<View> targets) {
            this.targets = targets;
            this.listener = listener;
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            targets.remove(v);
            v.removeOnLayoutChangeListener(this);
            if (targets.isEmpty()) {
                listener.onLayout();
            }
        }

        @Override
        public void dispose() {
            for (View target : targets) {
                target.removeOnLayoutChangeListener(this);
            }
            targets.clear();
        }
    }

    private static boolean isViewMeasured(View view) {
        return view.getHeight() > 0 && view.getWidth() > 0;
    }
}
