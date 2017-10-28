package com.mindcoders.phial.internal.overlay;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.overlay.OverlayView.OnPageSelectedListener;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phial.internal.util.SimpleAnimatorListener;

import java.util.List;

import static com.mindcoders.phial.internal.util.UiUtils.dpToPx;

public final class Overlay implements CurrentActivityProvider.AppStateListener {

    private static final int BUTTON_SIZE_DP = 68;
    private static final int STATUSBAR_HEIGHT_DP = 25;
    private static final int PAGE_MARGIN_DP = 8;

    private final PhialNotifier notifier;
    private final Context context;
    private final List<Page> pages;
    private final OverlayPositionStorage positionStorage;
    private final CurrentActivityProvider activityProvider;
    private final WindowManager windowManager;
    private final OverlayView overlayView;

    private ViewGroup containerWrapperView;
    private ViewGroup pageContainerView;

    private Point overlayViewPosition = new Point();

    private final Point displaySize = new Point();

    private final int btnSizePx;

    private boolean isOverlayViewSetup;

    private boolean isDrawOverlayPermissionRequested;

    public Overlay(
            Context context,
            List<Page> pages,
            PhialNotifier notifier,
            CurrentActivityProvider activityProvider,
            OverlayPositionStorage positionStorage) {
        this.context = context;
        this.pages = pages;
        this.notifier = notifier;
        this.activityProvider = activityProvider;
        this.positionStorage = positionStorage;

        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getSize(displaySize);

        btnSizePx = dpToPx(context, BUTTON_SIZE_DP);
        overlayView = new OverlayView(context, btnSizePx);

        activityProvider.addListener(this);
    }

    private void setupOverlayView(List<Page> pages) {
        overlayViewPosition = positionStorage.getPosition();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                btnSizePx,
                getType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.x = overlayViewPosition.x;
        params.y = overlayViewPosition.y;

        windowManager.addView(overlayView, params);

        overlayView.setOnPageSelectedListener(onPageSelectedListener);
        overlayView.setOnHandleMoveListener(onHandleMoveListener);

        overlayView.addPages(pages);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean canDrawOverlay() {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || Settings.canDrawOverlays(context);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void startSettingsActivity() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
        if (canDrawOverlay() && !isOverlayViewSetup) {
            setupOverlayView(pages);
            isOverlayViewSetup = true;
        } else if (!canDrawOverlay() && !isDrawOverlayPermissionRequested) {
            startSettingsActivity();
            isDrawOverlayPermissionRequested = true;
            return;
        }

        overlayView.setVisibility(View.VISIBLE);
        if (containerWrapperView != null) {
            containerWrapperView.setVisibility(View.VISIBLE);
        }
    }

    private void hide() {
        overlayView.setVisibility(View.GONE);
        if (containerWrapperView != null) {
            containerWrapperView.setVisibility(View.GONE);
        }
        positionStorage.savePosition(overlayViewPosition);
    }

    public void destroy() {
        activityProvider.removeListener(this);
        windowManager.removeView(overlayView);
        if (containerWrapperView != null) {
            windowManager.removeView(containerWrapperView);
            containerWrapperView = null;
            pageContainerView = null;
        }
    }

    private ViewGroup createWrapperView() {
        FrameLayout wrapper = new FrameLayout(context);

        int height = displaySize.y - btnSizePx - dpToPx(context, STATUSBAR_HEIGHT_DP);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height,
                getType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.y = displaySize.y / 2;

        wrapper.setLayoutParams(params);

        return wrapper;
    }

    private ViewGroup createPageContainerView() {
        CardView pageContainer = new CardView(context);
        pageContainer.setCardElevation(dpToPx(context, 4));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        int margin = dpToPx(context, PAGE_MARGIN_DP);
        params.leftMargin = margin;
        params.rightMargin = margin;
        params.bottomMargin = margin;

        pageContainer.setLayoutParams(params);

        return pageContainer;
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
            overlayViewPosition.x = params.x;
            overlayViewPosition.y = params.y;

            moveViewToTheEdge();
        }

        private void moveViewToTheEdge() {
            final int x;
            if (overlayViewPosition.x > 0) {
                x = displaySize.x / 2;
            } else {
                x = -displaySize.x / 2;
            }

            ValueAnimator animator = ValueAnimator.ofInt(overlayViewPosition.x, x);
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
        public void onFirstPageSelected(Page page) {
            notifier.fireDebugWindowShown();
            animateForward(page);
        }

        @Override
        public void onPageSelectionChanged(Page page) {
            pageContainerView.removeAllViews();
            pageContainerView.addView(page.getPageViewFactory().createPageView(context));
        }

        @Override
        public void onNothingSelected() {
            notifier.fireDebugWindowHide();
            animateBackward();
        }

        private void animateForward(final Page page) {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            int startX = params.x;
            int endX = displaySize.x / 2;

            int startY = params.y;
            int endY = -displaySize.y / 2;
            animate(startX, endX, startY, endY, overlayView, params,
                    new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            showContainerView();
                            pageContainerView.addView(page.getPageViewFactory().createPageView(context));
                        }
                    }
            );
        }

        private void showContainerView() {
            containerWrapperView = createWrapperView();
            pageContainerView = createPageContainerView();

            containerWrapperView.addView(pageContainerView);
            windowManager.addView(containerWrapperView, containerWrapperView.getLayoutParams());
        }

        private void animateBackward() {
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            int startX = params.x;
            int endX = overlayViewPosition.x;

            int startY = params.y;
            int endY = overlayViewPosition.y;
            animate(startX, endX, startY, endY, overlayView, params,
                    new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            windowManager.removeView(containerWrapperView);
                            containerWrapperView = null;
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
