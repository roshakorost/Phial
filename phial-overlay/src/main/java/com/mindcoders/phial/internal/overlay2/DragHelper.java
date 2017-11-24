package com.mindcoders.phial.internal.overlay2;

import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.mindcoders.phial.internal.util.NumberUtil;
import com.mindcoders.phial.internal.util.SimpleAnimatorListener;
import com.mindcoders.phial.internal.util.ViewUtil;

/**
 * Created by rost on 11/20/17.
 */

class DragHelper {
    private static final long ANIM_DURATION = 200L;
    private static final long CLICK_MAX_DURATION_MS = 300L;
    private static final float DEFAULT_X = 1f;
    private static final float DEFAULT_Y = 0.7f;

    private final PositionStorage positionStorage;

    DragHelper(PositionStorage positionStorage) {
        this.positionStorage = positionStorage;
    }

    void manageAnimated(WindowManager windowManager, View view, float relX, float relY) {
        final Dragger dragger = new Dragger(windowManager);
        dragger.moveToInitialPositionAnimated(relX, relY);
        view.setTag(dragger);
        view.setOnTouchListener(dragger);
        view.addOnLayoutChangeListener(dragger);
    }

    void manage(WindowManager windowManager, View view) {
        final Dragger dragger = new Dragger(windowManager);
        view.setTag(dragger);
        view.setOnTouchListener(dragger);
        view.addOnLayoutChangeListener(dragger);
    }

    void unmanage(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Dragger) {
            final Dragger dragger = (Dragger) tag;
            view.setOnTouchListener(null);
            view.removeOnLayoutChangeListener(dragger);
            dragger.cancelAnimation();
        }
    }

    void animateTo(View view, float relX, float relY, Runnable endAction) {
        final Object tag = view.getTag();
        final Dragger dragger;
        if (tag instanceof Dragger) {
            dragger = (Dragger) tag;
            dragger.animateToPosition(view, relX, relY, endAction);
        } else {
            throw new IllegalStateException("request to animate view that is not managed by DragHelper");
        }
    }

    private class Dragger implements View.OnTouchListener, View.OnLayoutChangeListener {
        private final WindowManager windowManager;
        //left and bottom is adjusted by view width and height
        private Rect parent = new Rect();
        private float initialTouchX, initialTouchY;
        private float initialX, initialY;
        private long startTimeMS;
        private ValueAnimator animator = ValueAnimator.ofInt();

        private boolean animated;
        private float relX;
        private float relY;

        Dragger(WindowManager windowManager) {
            this.windowManager = windowManager;

        }

        void moveToInitialPositionAnimated(float relX, float relY) {
            this.animated = true;
            this.relX = relX;
            this.relY = relY;
        }

        void cancelAnimation() {
            animator.cancel();
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (parent.isEmpty()) {
                return false;
            }

            animator.cancel();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    startTimeMS = event.getEventTime();
                    onMoveStart(v);
                    v.setPressed(true);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    animator.cancel();
                    onMove(v, event.getRawX() - initialTouchX, event.getRawY() - initialTouchY);
                    return true;
                case MotionEvent.ACTION_UP:
                    v.setPressed(false);
                    onMoveEnd(v);
                    final long downTimeMS = event.getEventTime() - startTimeMS;
                    final float halfButtonSize = Math.min(v.getHeight(), v.getWidth()) / 2f;
                    final boolean wasClicked = downTimeMS < CLICK_MAX_DURATION_MS
                            && ViewUtil.distance(initialTouchX, initialTouchY, event.getRawX(), event.getRawY()) < halfButtonSize;
                    if (wasClicked) {
                        v.performClick();
                    }
                    return true;
                default:
                    return true;
            }
        }

        private void onMoveStart(View view) {
            final LayoutParams lp = (LayoutParams) view.getLayoutParams();
            initialX = lp.x;
            initialY = lp.y;
        }

        private void onMove(View view, float dx, float dy) {
            final int newX = (int) NumberUtil.clipTo(initialX + dx, parent.left, parent.right);
            final int newY = (int) NumberUtil.clipTo(initialY + dy, parent.top, parent.bottom);
            setPosition(view, newX, newY);
        }

        private void onMoveEnd(View view) {
            final LayoutParams lp = (LayoutParams) view.getLayoutParams();
            final int x = lp.x;
            final int y = lp.y;
            final int edgeX;
            final float percentX;

            if (x < parent.centerX()) {
                edgeX = parent.left;
                percentX = 0f;
            } else {
                edgeX = parent.right;
                percentX = 1f;
            }

            cancelAnimation();
            animator = ValueAnimator.ofInt(x, edgeX).setDuration(ANIM_DURATION);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                int updatedX = (int) animation.getAnimatedValue();
                setPosition(view, updatedX, y);
            });
            animator.start();

            float percentY = (y - parent.top) / (float) parent.height();
            positionStorage.savePosition(percentX, percentY);
        }

        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            v.removeOnLayoutChangeListener(this);

            final Rect rect = new Rect();
            v.getWindowVisibleDisplayFrame(rect);
            parent = new Rect(0, 0, rect.width() - v.getWidth(), rect.height() - v.getHeight());

            final PositionStorage.Position position = positionStorage.getPosition(DEFAULT_X, DEFAULT_Y);
            final int targetX = (int) (parent.left + parent.width() * position.x);
            final int targetY = (int) (parent.top + parent.height() * position.y);

            if (animated) {
                final int startX = (int) (parent.left + parent.width() * relX);
                final int startY = (int) (parent.top + parent.height() * relY);
                ValueAnimator animator = createMoveAnimator(v, startX, startY, targetX, targetY);
                this.animator = animator;
                animator.start();
            } else {
                setPosition(v, targetX, targetY);
            }
        }

        private void setPosition(View view, int x, int y) {
            final LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.x = x;
            lp.y = y;
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            windowManager.updateViewLayout(view, lp);
        }

        void animateToPosition(View view, float relX, float relY, Runnable endAction) {
            cancelAnimation();

            final LayoutParams lp = (LayoutParams) view.getLayoutParams();

            final int targetX = (int) (parent.left + parent.width() * relX);
            final int targetY = (int) (parent.top + parent.height() * relY);

            animator = createMoveAnimator(view, lp.x, lp.y, targetX, targetY);
            animator.addListener(SimpleAnimatorListener.createEndListener(endAction));

            animator.start();
        }

        @NonNull
        private ValueAnimator createMoveAnimator(View v, int startX, int startY, int targetX, int targetY) {
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(ANIM_DURATION);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                float fraction = (float) animation.getAnimatedValue();
                final int x = (int) (startX + (targetX - startX) * fraction);
                final int y = (int) (startY + (targetY - startY) * fraction);
                setPosition(v, x, y);
            });
            return animator;
        }

    }
}
