package com.mindcoders.phial.internal.overlay;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.keyvalue.KeyValueView;
import com.mindcoders.phial.internal.overlay.OverlayView.OnPageSelectedListener;
import com.mindcoders.phial.internal.share.ShareView;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phial.internal.util.SimpleAnimatorListener;

import java.io.File;
import java.util.Collections;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public final class Overlay implements CurrentActivityProvider.AppStateListener {
    private final static int BUTTON_SIZE = 160;

    private final PhialNotifier notifier;
    private final Context context;

    final WindowManager windowManager;

    final OverlayView overlayView;

    int overlayViewX;

    int overlayY;

    private final Point displaySize = new Point();

    private ViewGroup pageContainer;

    public Overlay(PhialNotifier notifier, final Context context) {
        this.notifier = notifier;
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        overlayView = new OverlayView(context);
        overlayView.setOnHandleTouchListener(overlayOnTouchListener);

        addButton(overlayView);

        overlayView.setOnPageSelectedListener(onPageSelectedListener);

        overlayView.addPage(new OverlayView.Page(
                R.drawable.ic_keyvalue,
                new PageViewFactory<KeyValueView>() {
                    @Override
                    public KeyValueView onPageCreate() {
                        return new KeyValueView(context);
                    }

                    @Override
                    public void onPageDestroy(KeyValueView view) {
                        view.onDestroy();
                    }
                }
        ));

        overlayView.addPage(new OverlayView.Page(
                R.drawable.ic_keyvalue,
                new PageViewFactory<ShareView>() {
                    @Override
                    public ShareView onPageCreate() {
                        ShareView shareView = new ShareView(context);
                        shareView.setFiles(Collections.<File>emptyList());
                        return shareView;
                    }

                    @Override
                    public void onPageDestroy(ShareView view) {

                    }
                }
        ));
    }

    private void addButton(View view) {
        int permissionFlag = getFlag();

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                160,
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

    private void show() {
        overlayView.setVisibility(View.VISIBLE);
    }

    private void hide() {
        overlayView.setVisibility(View.GONE);
    }

    public void destroy() {
        windowManager.removeView(overlayView);
    }

    private void createPageContainer() {
        FrameLayout frameLayout = new FrameLayout(context);

        int height = displaySize.y - BUTTON_SIZE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height,
                getFlag(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.y = displaySize.y / 2;

        windowManager.addView(frameLayout, params);

        pageContainer = frameLayout;
    }

    private OnTouchListener overlayOnTouchListener = new OnTouchListener() {

        private int initialX;
        private int initialY;

        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View __, MotionEvent event) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = params.x;
                    initialY = params.y;

                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    overlayViewX = params.x;
                    overlayY = params.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(overlayView, params);
                    break;
            }

            return false;
        }
    };


    private final OnPageSelectedListener onPageSelectedListener = new OnPageSelectedListener() {

        @Override
        public void onFirstPageSelected(OverlayView.Page page) {
            notifier.fireDebugWindowShown();
            animateForward(page);
        }

        @Override
        public void onPageSelectionChanged(OverlayView.Page page) {
            notifier.fireDebugWindowHide();
            pageContainer.removeAllViews();
            pageContainer.addView(page.pageViewFactory.onPageCreate());
        }

        @Override
        public void onNothingSelected() {
            animateBackward();
        }

        private void animateForward(final OverlayView.Page page) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            int startX = params.x;
            int endX = displaySize.x / 2;

            int startY = params.y;
            int endY = -displaySize.y / 2;
            animate(startX, endX, startY, endY, overlayView, params,
                    new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            createPageContainer();
                            pageContainer.addView(page.pageViewFactory.onPageCreate());
                        }
                    }
            );
        }

        private void animateBackward() {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            int startX = params.x;
            int endX = overlayViewX;

            int startY = params.y;
            int endY = overlayY;
            animate(startX, endX, startY, endY, overlayView, params,
                    new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            windowManager.removeView(pageContainer);
                        }
                    }
            );
        }

        private void animate(
                int startX, int endX, int startY, int endY, final View view, final WindowManager.LayoutParams params,
                Animator.AnimatorListener listener
        ) {
            PropertyValuesHolder x = PropertyValuesHolder.ofInt("x", startX, endX);
            PropertyValuesHolder y = PropertyValuesHolder.ofInt("y", startY, endY);
            ValueAnimator valueAnimator = ValueAnimator.ofPropertyValuesHolder(x, y);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    params.x = (int) animation.getAnimatedValue("x");
                    params.y = (int) animation.getAnimatedValue("y");
                    windowManager.updateViewLayout(view, params);
                }
            });
            valueAnimator.setDuration(200);
            valueAnimator.addListener(listener);
            valueAnimator.start();
        }

    };

    @Override
    public void onAppForeground() {
        show();
    }

    @Override
    public void onAppBackground() {
        hide();
    }
}
