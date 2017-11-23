package com.mindcoders.phial.internal.overlay2;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Map<Activity, PhialButton> buttons = new HashMap<>();
    private final DragHelper dragHelper;
    private final ExpandedView expandedView;
    private final List<Page> pages;
    private final PhialNotifier notifier;
    private final Context context;

    private boolean isExpanded = false;
    private Activity curActivity;

    public OverlayPresenter(Context baseContext, List<Page> pages, PositionStorage positionStorage, PhialNotifier notifier) {
        this.context = new ContextThemeWrapper(baseContext, R.style.Theme_Phial);
        this.pages = pages;
        this.notifier = notifier;
        this.dragHelper = new DragHelper(positionStorage);
        this.expandedView = new ExpandedView(context, this);

        //this.dragHelper = DragHelper.create(button, sharedPreferences);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (!isExpanded) {
            showButton(activity);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!isExpanded) {
            removePhialButton(activity);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        curActivity = activity;
        if (isExpanded) {
            showExpandView(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ObjectUtil.equals(activity, curActivity)) {
            if (isExpanded) {
                removeExpandedView(curActivity, false);
            }

            curActivity = null;
        }
    }

    private void showDebugWindow() {
        notifier.fireDebugWindowShown();

        removePhialButton(curActivity);
        showExpandView(curActivity);

        isExpanded = true;
    }

    private void closeDebugWindow() {
        notifier.fireDebugWindowHide();

        removeExpandedView(curActivity, true);
        showButton(curActivity);

        isExpanded = false;
    }

    private void showExpandView(Activity activity) {
        activity.getWindowManager().addView(expandedView, wrap(EXPANDED_VIEW_PARAMS));
        expandedView.displayPages(pages, pages.get(1));
    }

    private void removeExpandedView(Activity activity, boolean destroyContent) {
        if (destroyContent) {
            expandedView.destroyContent();
        }
        activity.getWindowManager().removeView(expandedView);
    }

    private void showButton(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        final PhialButton button = createButton();
        buttons.put(activity, button);
        final LayoutParams wrap = wrap(BUTTON_PARAMS);
        windowManager.addView(button, wrap);
        dragHelper.manager(windowManager, button);
    }

    private void removePhialButton(Activity activity) {
        final PhialButton button = buttons.remove(activity);
        dragHelper.unmanage(button);
        activity.getWindowManager().removeView(button);
    }

    private PhialButton createButton() {
        final PhialButton button = new PhialButton(context);
        button.setIcon(R.drawable.ic_handle);
        button.setOnClickListener(v -> showDebugWindow());
        return button;
    }

    @Override
    public void finish() {
        if (isExpanded) {
            closeDebugWindow();
        }
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
