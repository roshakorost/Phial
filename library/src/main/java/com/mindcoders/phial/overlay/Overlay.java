package com.mindcoders.phial.overlay;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public final class Overlay {

    final WindowManager windowManager;

    final Button overlayButton;

    public Overlay(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        overlayButton = new Button(context);
        overlayButton.setOnTouchListener(overlayOnTouchListener);
        overlayButton.setVisibility(View.VISIBLE);

        addViewToWindow(overlayButton);
    }

    private void addViewToWindow(View view) {
        int permissionFlag = getFlag();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                permissionFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );


        windowManager.addView(view, params);

    }

    private int getFlag() {
        int permissionFlag;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissionFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            permissionFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        return permissionFlag;
    }

    public void show() {
        overlayButton.setVisibility(View.VISIBLE);
    }

    public void hide() {
        overlayButton.setVisibility(View.GONE);
    }

    public void destroy() {
        windowManager.removeView(overlayButton);
    }

    private OnTouchListener overlayOnTouchListener = new OnTouchListener() {

        private int initialX;
        private int initialY;

        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) v.getLayoutParams();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(v, params);
                    break;
            }

            return false;
        }
    };

}
