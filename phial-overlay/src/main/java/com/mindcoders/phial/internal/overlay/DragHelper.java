package com.mindcoders.phial.internal.overlay;

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
    private static final long ANIM_DURATION = 250L;
    private static final long CLICK_MAX_DURATION_MS = 300L;
    private static final float DEFAULT_X = 1f;
    private static final float DEFAULT_Y = 0.7f;

    private final PositionStorage positionStorage;
    private final int animatedXPos;
    private final int animatedYPos;

    DragHelper(PositionStorage positionStorage, int animatedXPos, int animatedYPos) {
        this.positionStorage = positionStorage;
        this.animatedXPos = animatedXPos;
        this.animatedYPos = animatedYPos;
    }

    void manage(WindowManager windowManager, View view) {
        final Dragger dragger = new Dragger(view, windowManager);
        view.setTag(dragger);
        dragger.start();
    }

    void unmanage(View view) {
        Dragger dragger = findDragger(view);
        dragger.stop();
    }

    void animateFromDefaultPosition(View view, Runnable endAction) {
        findDragger(view).animateFromDefaultPosition(endAction);
    }

    void animateToDefaultPosition(View view, Runnable endAction) {
        findDragger(view).animateToDefaultPosition(endAction);
    }

    private Dragger findDragger(View view) {
        final Object tag = view.getTag();
        final Dragger dragger;
        if (tag instanceof Dragger) {
            return (Dragger) tag;
        } else {
            throw new IllegalStateException("findDragger for unmanaged view" + tag);
        }
    }

    private class Dragger {
        private final WindowManager windowManager;
        private final View view;
        //left and bottom is adjusted by view width and height
        private Rect parent = new Rect();
        private float initialTouchX, initialTouchY;
        private float initialX, initialY;
        private long startTimeMS;
        private LayoutHelper.Disposable disposable = LayoutHelper.Disposable.EMPTY;

        private ValueAnimator animator = ValueAnimator.ofInt();

        Dragger(View view, WindowManager windowManager) {
            this.view = view;
            this.windowManager = windowManager;
        }

        void start() {
            view.setOnTouchListener(this::onTouch);
            disposable = LayoutHelper.onLayout(this::setInitialPosition, view);
        }

        void stop() {
            view.setOnTouchListener(null);
            disposable.dispose();
            animator.cancel();
        }

        void animateFromDefaultPosition(Runnable endAction) {
            disposable.dispose();
            disposable = LayoutHelper.onLayout(() -> animateFromDefaultToInitial(false, endAction));
        }

        void animateToDefaultPosition(Runnable endAction) {
            disposable.dispose();
            disposable = LayoutHelper.onLayout(() -> animateFromDefaultToInitial(true, endAction));
        }

        private void animateFromDefaultToInitial(boolean reversed, Runnable endAction) {
            initParentRect();
            animator.cancel();

            final int startX = parent.right - animatedXPos;
            final int startY = parent.top - animatedYPos;

            final PositionStorage.Position position = positionStorage.getPosition(DEFAULT_X, DEFAULT_Y);
            final int targetX = (int) (parent.left + parent.width() * position.x);
            final int targetY = (int) (parent.top + parent.height() * position.y);

            if (reversed) {
                animator = createMoveAnimator(view, targetX, targetY, startX, startY);
            } else {
                animator = createMoveAnimator(view, startX, startY, targetX, targetY);
            }
            if (endAction != null) {
                animator.addListener(SimpleAnimatorListener.createEndListener(endAction));
            }
            animator.start();
        }

        private void animateToPosition(View view, float relX, float relY) {
            animator.cancel();

            final LayoutParams lp = (LayoutParams) view.getLayoutParams();
            final int targetX = (int) (parent.left + parent.width() * relX);
            final int targetY = (int) (parent.top + parent.height() * relY);

            animator = createMoveAnimator(view, lp.x, lp.y, targetX, targetY);
            animator.start();
        }

        private void initParentRect() {
            final Rect rect = new Rect();
            view.getWindowVisibleDisplayFrame(rect);
            parent = new Rect(0, 0, rect.width() - view.getWidth(), rect.height() - view.getHeight());
        }

        private void setPosition(View view, int x, int y) {
            final LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.x = x;
            lp.y = y;
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            windowManager.updateViewLayout(view, lp);
        }

        private void setInitialPosition() {
            initParentRect();

            final PositionStorage.Position position = positionStorage.getPosition(DEFAULT_X, DEFAULT_Y);
            final int targetX = (int) (parent.left + parent.width() * position.x);
            final int targetY = (int) (parent.top + parent.height() * position.y);
            setPosition(view, targetX, targetY);
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


        private boolean onTouch(View v, MotionEvent event) {
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
            final float percentX;

            if (x < parent.centerX()) {
                percentX = 0f;
            } else {
                percentX = 1f;
            }
            float percentY = (y - parent.top) / (float) parent.height();
            animateToPosition(view, percentX, percentY);
            positionStorage.savePosition(percentX, percentY);
        }
    }
}
