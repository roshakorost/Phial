package com.mindcoders.phial.internal.overlay2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

import java.util.List;

import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.MATCH_PARENT;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION;
import static android.view.WindowManager.LayoutParams.WRAP_CONTENT;


/**
 * Created by rost on 11/20/17.
 */

public class OverlayPresenter extends SimpleActivityLifecycleCallbacks implements ExpandedView.ExpandedViewCallback {
    private final static int WINDOW_TYPE = TYPE_APPLICATION;
    private final static LayoutParams BUTTON_PARAMS = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT,
            WINDOW_TYPE,
            FLAG_NOT_FOCUSABLE | FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT
    );
    private final static LayoutParams EXPANDED_VIEW_PARAMS = new LayoutParams(MATCH_PARENT, MATCH_PARENT,
            WINDOW_TYPE,
            FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
    );

    static {
        EXPANDED_VIEW_PARAMS.dimAmount = 0f;
        BUTTON_PARAMS.dimAmount = 0f;
    }

    private final DragHelper dragHelper;
    private final List<Page> pages;
    private final PhialButton button;
    private final ExpandedView expandedView;
    private final PhialNotifier notifier;

    private boolean isExpanded = false;
    private Activity curActivity;

    public OverlayPresenter(Context baseContext, List<Page> pages, SharedPreferences sharedPreferences, PhialNotifier notifier) {
        final Context context = new ContextThemeWrapper(baseContext, R.style.Theme_Phial);
        this.pages = pages;
        this.notifier = notifier;
        this.expandedView = new ExpandedView(context, this);
        this.button = new PhialButton(context);
        this.button.setIcon(R.drawable.ic_handle);
        button.setOnClickListener(v -> showDebugWindow());
        this.dragHelper = DragHelper.create(button, sharedPreferences);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        curActivity = activity;
        showView(activity, isExpanded);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ObjectUtil.equals(activity, curActivity)) {
            hideView(activity, isExpanded);
        }
    }

    private void showDebugWindow() {
        notifier.fireDebugWindowShown();

        hideView(curActivity, false);
        showView(curActivity, true);
        isExpanded = true;
    }

    private void closeDebugWindow() {
        notifier.fireDebugWindowHide();

        hideView(curActivity, true);
        showView(curActivity, false);
        isExpanded = false;
    }

    private void hideView(Activity activity, boolean expanded) {
        if (expanded) {
            activity.getWindowManager().removeView(expandedView);
        } else {
            activity.getWindowManager().removeView(button);
        }
    }

    private void showView(Activity activity, boolean expanded) {
        if (expanded) {
            activity.getWindowManager().addView(expandedView, wrap(EXPANDED_VIEW_PARAMS));
            expandedView.displayPages(pages, pages.get(0));
        } else {
            activity.getWindowManager().addView(button, wrap(BUTTON_PARAMS));
        }
    }

    @Override
    public void finish() {
        if (isExpanded) {
            closeDebugWindow();
        }
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
            expandedView.displayPages(pages, page);
        }
    }

    private static LayoutParams wrap(LayoutParams source) {
        LayoutParams lp = new LayoutParams();
        lp.copyFrom(source);
        return lp;
    }
}
