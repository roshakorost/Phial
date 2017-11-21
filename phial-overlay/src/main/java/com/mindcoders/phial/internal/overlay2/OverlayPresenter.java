package com.mindcoders.phial.internal.overlay2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by rost on 11/20/17.
 */

public class OverlayPresenter extends SimpleActivityLifecycleCallbacks implements ExpandedView.ExpandedViewCallback {
    private static final int GRAVITY = Gravity.TOP | Gravity.START;
    private final DragHelper dragHelper;
    private final List<Page> pages;
    private final PhialButton button;
    private final ExpandedView expandedView;
    private final Context context;
    private final PhialNotifier notifier;

    private boolean isExpanded = false;
    private Activity curActivity;
    private LayoutHelper.Disposable disposable = LayoutHelper.Disposable.EMPTY;

    public OverlayPresenter(Context baseContext, List<Page> pages, SharedPreferences sharedPreferences, PhialNotifier notifier) {
        context = new ContextThemeWrapper(baseContext, R.style.Theme_Phial);
        this.pages = pages;
        this.notifier = notifier;
        this.expandedView = new ExpandedView(context);
        this.button = new PhialButton(context);
        this.button.setIcon(R.drawable.ic_handle);
        button.setOnClickListener(v -> showDebugWindow());
        this.dragHelper = DragHelper.create(button, sharedPreferences);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        final FrameLayout root = getRootView(activity);
        disposable = LayoutHelper.onLayout(() -> onActivityReady(activity), root);
    }

    private void onActivityReady(Activity newActivity) {
        final FrameLayout newRoot = getRootView(newActivity);
        final boolean activityChanged = !ObjectUtil.equals(newActivity, curActivity);

        if (curActivity != null && activityChanged) {
            removeViews(curActivity);
        }

        if (activityChanged) {
            if (isExpanded) {
                showExpandView(newRoot);
            } else {
                showButtonView(newRoot);
            }
        }

        curActivity = newActivity;

        final Rect newBounds = getBounds(newRoot);
        dragHelper.adjustBounds(newBounds);
        expandedView.adjustBounds(newRoot.getWidth(), newRoot.getHeight(), newBounds);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        dragHelper.cancelAnimation();
        if (ObjectUtil.equals(activity, curActivity)) {
            disposable.dispose();
        }
    }

    private void removeViews(@NonNull Activity activity) {
        final FrameLayout root = getRootView(activity);
        root.removeView(isExpanded ? expandedView : button);
    }

    private void showExpandView(FrameLayout root) {
        root.addView(expandedView, new LayoutParams(MATCH_PARENT, MATCH_PARENT, GRAVITY));
        expandedView.displayPages(this, pages, pages.get(1));
    }

    private void showButtonView(FrameLayout root) {
        root.addView(button, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, GRAVITY));
    }

    private void showDebugWindow() {
        notifier.fireDebugWindowShown();

        final FrameLayout root = getRootView(curActivity);
        dragHelper.cancelAnimation();
        root.removeView(button);

        showExpandView(root);
        isExpanded = true;
    }

    private void closeDebugWindow() {
        notifier.fireDebugWindowHide();

        final FrameLayout root = getRootView(curActivity);
        root.removeView(expandedView);
        expandedView.reset();

        showButtonView(root);
        isExpanded = false;
    }

    @Override
    public void finish() {
        if (isExpanded) {
            closeDebugWindow();
        }
    }

    private static FrameLayout getRootView(Activity activity) {
        return (FrameLayout) activity.findViewById(android.R.id.content).getRootView();
    }

    private static Rect getBounds(View view) {
        final Rect outRect = new Rect();
        view.getWindowVisibleDisplayFrame(outRect);
        return outRect;
    }

    @Override
    public Activity getCurrentActivity() {
        return curActivity;
    }

    @Override
    public void onPageSelected(Page page) {
        if (isExpanded) {
            expandedView.displayPages(this, pages, page);
        }
    }
}
