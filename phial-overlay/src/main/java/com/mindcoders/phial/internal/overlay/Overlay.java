package com.mindcoders.phial.internal.overlay;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.Screen;
import com.mindcoders.phial.internal.ScreenTracker;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.overlay.OverlayView.OnPageSelectedListener;
import com.mindcoders.phial.internal.util.AnimatorFactory;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phial.internal.util.SimpleAnimatorListener;
import com.mindcoders.phial.internal.util.support.ViewCompat;

import java.util.List;

import static com.mindcoders.phial.internal.util.UiUtils.dpToPx;

public final class Overlay implements CurrentActivityProvider.AppStateListener, ScreenTracker.ScreenListener {

    private static final int BUTTON_SIZE_DP = 68;
    private static final int STATUSBAR_HEIGHT_DP = 25;

    private final PhialNotifier notifier;
    private final Context context;
    private final List<Page> pages;
    private final OverlayPositionStorage positionStorage;
    private final CurrentActivityProvider activityProvider;
    private final ScreenTracker screenTracker;
    private final WindowManager windowManager;
    private final OverlayView overlayView;

    private ViewGroup containerWrapperView;
    private PageContainerView pageContainerView;

    private View selectedPageIndicator;

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
            ScreenTracker screenTracker,
            OverlayPositionStorage positionStorage,
            SelectedPageStorage selectedPageStorage
    ) {
        this.context = context;
        this.pages = pages;
        this.notifier = notifier;
        this.activityProvider = activityProvider;
        this.screenTracker = screenTracker;
        this.positionStorage = positionStorage;

        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getSize(displaySize);

        btnSizePx = dpToPx(context, BUTTON_SIZE_DP);
        overlayView = new OverlayView(context, btnSizePx, selectedPageStorage);

        activityProvider.addListener(this);
        screenTracker.addListener(this);
    }

    private void setupOverlayView(List<Page> pages) {
        overlayViewPosition = positionStorage.getPosition(
                displaySize.x / 2,
                (int) ((displaySize.y / 2) * -0.3)
        );
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                btnSizePx,
                getType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                PixelFormat.TRANSLUCENT
        );

        params.x = overlayViewPosition.x;
        params.y = overlayViewPosition.y;
        params.dimAmount = 0.0f;

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

        if (isOverlayPositionPointSet(overlayViewPosition)) {
            positionStorage.savePosition(overlayViewPosition);
        }
    }

    private boolean isOverlayPositionPointSet(Point point) {
        return point.x != 0 && point.y != 0;
    }

    public void destroy() {
        activityProvider.removeListener(this);
        screenTracker.removeListener(this);
        windowManager.removeView(overlayView);
        if (containerWrapperView != null) {
            windowManager.removeView(containerWrapperView);
            containerWrapperView = null;
            pageContainerView = null;
            selectedPageIndicator = null;
        }
    }

    private ViewGroup createWrapperView() {
        WrapperView wrapper = new WrapperView(context);

        int height = displaySize.y - btnSizePx - dpToPx(context, STATUSBAR_HEIGHT_DP);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height,
                getType(),
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        params.y = displaySize.y / 2;
        params.dimAmount = 0.5f;

        wrapper.setLayoutParams(params);

        wrapper.setOnBackPressedListener(onBackPressedListener);

        return wrapper;
    }

    private PageContainerView createPageContainerView() {
        PageContainerView pageContainer = new PageContainerView(context);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        params.topMargin = dpToPx(context, 10);

        int sideMargin = dpToPx(context, 5);
        params.leftMargin = sideMargin;
        params.rightMargin = sideMargin;
        params.bottomMargin = sideMargin;

        pageContainer.setLayoutParams(params);

        pageContainer.setBackgroundResource(R.drawable.bg_page_container);


        ViewCompat.setElevation(pageContainer, 5f);

        return pageContainer;
    }

    private final WrapperView.OnBackPressedListener onBackPressedListener = new WrapperView.OnBackPressedListener() {

        @Override
        public void onBackPressed() {
            if (!pageContainerView.onBackPressed()) {
                overlayView.hide();
            }
        }

    };

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
        public void onFirstPageSelected(Page page, int position) {
            notifier.fireDebugWindowShown();
            animateForward(page, position);
        }

        @Override
        public void onPageSelectionChanged(Page page, int position) {
            pageContainerView.showPage(page.getPageViewFactory().createPageView(context, overlayCallback, screenTracker));
            pageContainerView.setPageTitle(page.getTitle());

            updateSelectedPageIndicator(position);
        }

        @Override
        public void onNothingSelected() {
            notifier.fireDebugWindowHide();
            animateBackward();
        }

        private void animateForward(final Page page, int position) {
            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            final int startX = params.x;
            final int endX = displaySize.x / 2;

            final int startY = params.y;
            final int endY = -displaySize.y / 2;

            showContainerView();

            showSelectedPageIndicator(position);

            // TODO: 10/30/17 figure out a way to avoid post()
            containerWrapperView.post(new Runnable() {
                @Override
                public void run() {
                    animate(startX, endX, startY, endY,
                            true, params,
                            new SimpleAnimatorListener() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    containerWrapperView.setVisibility(View.VISIBLE);
                                    pageContainerView.showPage(
                                            page.getPageViewFactory().createPageView(context, overlayCallback, screenTracker)
                                    );
                                    pageContainerView.setPageTitle(page.getTitle());
                                    params.dimAmount = 0.5f;
                                    windowManager.updateViewLayout(overlayView, params);
                                }
                            }
                    );
                }
            });
        }

        private void showContainerView() {
            containerWrapperView = createWrapperView();
            pageContainerView = createPageContainerView();

            containerWrapperView.setVisibility(View.GONE);

            containerWrapperView.addView(pageContainerView);
            windowManager.addView(containerWrapperView, containerWrapperView.getLayoutParams());
        }

        private void showSelectedPageIndicator(int positionOffset) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    dpToPx(context, 10),
                    dpToPx(context, 10)
            );

            params.leftMargin = getSelectedPageIndicatorMargin(positionOffset + 1);

            selectedPageIndicator = new View(context);
            selectedPageIndicator.setBackgroundResource(R.drawable.active_page_arrow);

            containerWrapperView.addView(selectedPageIndicator, params);
        }

        private int getSelectedPageIndicatorMargin(int positionOffset) {
            return displaySize.x - positionOffset * dpToPx(context, BUTTON_SIZE_DP) - (dpToPx(context, BUTTON_SIZE_DP) / 2)
                    - dpToPx(context, 5);
        }

        private void updateSelectedPageIndicator(int position) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) selectedPageIndicator.getLayoutParams();
            params.leftMargin = getSelectedPageIndicatorMargin(position + 1);
            selectedPageIndicator.setLayoutParams(params);
        }

        private void animateBackward() {
            final WindowManager.LayoutParams params = (WindowManager.LayoutParams) overlayView.getLayoutParams();
            int startX = params.x;
            int endX = overlayViewPosition.x;

            int startY = params.y;
            int endY = overlayViewPosition.y;
            animate(startX, endX, startY, endY,
                    false, params,
                    new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            // hide view before removing to avoid flickering
                            containerWrapperView.setVisibility(View.GONE);
                            windowManager.removeView(containerWrapperView);
                            containerWrapperView = null;
                            pageContainerView = null;
                            params.dimAmount = 0.0f;
                            windowManager.updateViewLayout(overlayView, params);
                        }
                    }
            );
        }

        private void animate(
                int startX, int endX,
                int startY, int endY,
                boolean isAppearing,
                final WindowManager.LayoutParams params,
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
                    windowManager.updateViewLayout(overlayView, params);
                }
            });
            valueAnimator.setDuration(300);

            AnimatorFactory factory = AnimatorFactory.createFactory(overlayView);
            AnimatorSet animator = new AnimatorSet();
            if (isAppearing) {
                valueAnimator.addListener(listener);
                Animator appearAnimator = factory.createAppearAnimator(containerWrapperView);
                appearAnimator.setDuration(300);
                animator.play(valueAnimator).before(appearAnimator);
            } else {
                Animator disappearAnimator = factory.createDisappearAnimator(containerWrapperView);
                disappearAnimator.addListener(listener);
                disappearAnimator.setDuration(300);
                animator.play(valueAnimator).after(disappearAnimator);
            }

            animator.start();
        }

    };

    private final OverlayCallback overlayCallback = new OverlayCallback() {

        @Override
        public void finish() {
            overlayView.hide();
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

    @Override
    public void onScreenChanged(Screen screen) {
        overlayView.updateVisiblePages(screen);
    }

}
