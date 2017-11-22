package com.mindcoders.phial.internal.overlay2;

import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.mindcoders.phial.internal.util.AnimationUtil;
import com.mindcoders.phial.internal.util.NumberUtil;
import com.mindcoders.phial.internal.util.ViewUtil;

/**
 * Created by rost on 11/20/17.
 */

class DragHelper implements View.OnTouchListener {
    private static final long CLICK_MAX_DURATION_MS = 300L;
    private static final float DEFAULT_X = 1f;
    private static final float DEFAULT_Y = 0.7f;

    private final PositionStorage positionStorage;
    private final View target;

    //left and bottom is adjusted by view width and height
    private Rect parent = new Rect();
    private LayoutHelper.Disposable disposable = LayoutHelper.Disposable.EMPTY;

    private float initialTouchX, initialTouchY;
    private float initialX, initialY;
    private long startTimeMS;

    DragHelper(View view, PositionStorage positionStorage) {
        this.target = view;
        this.positionStorage = positionStorage;
    }

    static DragHelper create(View view, SharedPreferences sp) {
        final PositionStorage positionStorage = new PositionStorage(sp);
        return new DragHelper(view, positionStorage);
    }


    void adjustBounds(Rect newBounds) {
        target.setOnTouchListener(this);
        disposable.dispose();
        disposable = LayoutHelper.onLayout(() -> onLayout(newBounds), target);
    }

    void cancelAnimation() {
        target.animate().cancel();
    }

    void start() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (parent.isEmpty()) {
            return false;
        }

        target.animate().cancel();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                startTimeMS = event.getEventTime();
                onMoveStart();
                target.setPressed(true);
                return true;
            case MotionEvent.ACTION_MOVE:
                target.animate().cancel();
                onMove(event.getRawX() - initialTouchX, event.getRawY() - initialTouchY);
                return true;
            case MotionEvent.ACTION_UP:
                target.setPressed(false);
                onMoveEnd();
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

    private void onMoveStart() {
        initialX = target.getX();
        initialY = target.getY();
    }

    private void onMove(float dx, float dy) {
        float newX = NumberUtil.clipTo(initialX + dx, parent.left, parent.right);
        float newY = NumberUtil.clipTo(initialY + dy, parent.top, parent.bottom);
        target.setX(newX);
        target.setY(newY);
    }

    private void onMoveEnd() {
        final float edgeX, percentX;
        if (target.getX() < parent.centerX()) {
            edgeX = parent.left;
            percentX = 0f;
        } else {
            edgeX = parent.right;
            percentX = 1f;
        }
        AnimationUtil.animateX(target, edgeX);

        float percentY = (target.getY() - parent.top) / parent.height();
        positionStorage.savePosition(percentX, percentY);
    }

    private void onLayout(Rect parentBounds) {
        parent = new Rect(parentBounds);

        parent.right -= target.getWidth();
        parent.bottom -= target.getHeight();

        final PositionStorage.Position position = positionStorage.getPosition(DEFAULT_X, DEFAULT_Y);
        final float x = parent.left + parent.width() * position.x;
        final float y = parent.top + parent.height() * position.y;
        target.setX(x);
        target.setY(y);
    }
}
