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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static com.mindcoders.phial.internal.util.UiUtils.dpToPx;

public final class Overlay implements CurrentActivityProvider.AppStateListener {

    private final static int BUTTON_SIZE = 160;

    private static final int STATUSBAR_HEIGHT = 25; //dp


    private final PhialNotifier notifier;
    private final Context context;

    private final WindowManager windowManager;

    private final OverlayView overlayView;

    private FrameLayout pageContainerView;

    private int overlayViewX, overlayViewY;

    private final Point displaySize = new Point();

    private final int btnSizePx;

    public Overlay(PhialNotifier notifier, final Context context) {
        this.notifier = notifier;
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(displaySize);

        btnSizePx = dpToPx(context, BUTTON_SIZE);

        overlayView = new OverlayView(context, btnSizePx);
        overlayView.setOnHandleMoveListener(onHandleMoveListener);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                btnSizePx,
                getType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        windowManager.addView(overlayView, params);

        overlayView.setOnPageSelectedListener(onPageSelectedListener);

        overlayView.addPage(new OverlayView.Page(
                R.drawable.ic_keyvalue,
                new PageViewFactory<KeyValueView>() {

                    @Override
                    public KeyValueView createPageView() {
                        return new KeyValueView(context);
                    }

                }
        ));

        overlayView.addPage(new OverlayView.Page(
                R.drawable.ic_share,
                new PageViewFactory<ShareView>() {

                    @Override
                    public ShareView createPageView() {
                        ShareView shareView = new ShareView(context);
                        shareView.setFiles(Collections.<File>emptyList());
                        return shareView;
                    }

                }
        ));
    }

    private int getType() {
        final int type;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        return type;
    }

    private void show() {
        overlayView.setVisibility(View.VISIBLE);
        if (pageContainerView != null) {
            pageContainerView.setVisibility(View.VISIBLE);
        }
    }

    private void hide() {
        overlayView.setVisibility(View.GONE);
        if (pageContainerView != null) {
            pageContainerView.setVisibility(View.GONE);
        }
    }

    public void destroy() {
        throw new IllegalArgumentException("NOT implemented yet");
    }

    private FrameLayout createPageContainerView() {
        FrameLayout pageContainterView = new FrameLayout(context);

        int height = displaySize.y - btnSizePx - dpToPx(context, STATUSBAR_HEIGHT);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height,
                getType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.y = displaySize.y / 2;

        windowManager.addView(pageContainterView, params);

        return pageContainterView;
    }

    private final OverlayView.OnHandleMoveListener onHandleMoveListener = new OverlayView.OnHandleMoveListener() {

        private int initialX, initialY;

        @Override
        public void onMoveStart(float x, float y) {

            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            initialX = params.x;
            initialY = params.y;
        }

        @Override
        public void onMove(float dx, float dy) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            params.x = initialX + (int) dx;
            params.y = initialY + (int) dy;
            windowManager.updateViewLayout(overlayView, params);
        }

        @Override
        public void onMoveEnd() {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            overlayViewX = params.x;
            overlayViewY = params.y;

            Log.i("viewPosition", String.format("x = %s, y = %s", overlayViewX, overlayViewY));

            moveViewToTheEdge();
        }

        private void moveViewToTheEdge() {
            final int x;
            if (overlayViewX > 0) {
                x = displaySize.x / 2;
            } else {
                x = -displaySize.x / 2;
            }

            ValueAnimator animator = ValueAnimator.ofInt(overlayViewX, x);
            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    params.x = (int) animation.getAnimatedValue();
                    windowManager.updateViewLayout(overlayView, params);
                }
            });
            animator.setDuration(100);
            animator.start();
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
            pageContainerView.removeAllViews();
            pageContainerView.addView(page.pageViewFactory.createPageView());
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
                            pageContainerView = createPageContainerView();
                            pageContainerView.addView(page.pageViewFactory.createPageView());
                        }
                    }
            );
        }

        private void animateBackward() {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            int startX = params.x;
            int endX = overlayViewX;

            int startY = params.y;
            int endY = overlayViewY;
            animate(startX, endX, startY, endY, overlayView, params,
                    new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            windowManager.removeView(pageContainerView);
                            pageContainerView = null;
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
