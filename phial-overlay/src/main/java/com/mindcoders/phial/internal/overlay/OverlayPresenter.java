package com.mindcoders.phial.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.Screen;
import com.mindcoders.phial.internal.ScreenTracker;
import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

import java.util.ArrayList;
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

public class OverlayPresenter extends SimpleActivityLifecycleCallbacks
        implements ExpandedView.ExpandedViewCallback, ScreenTracker.ScreenListener {

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

    //ExpandView has state so we don't want to recreate it after pause/resume and orientation change,
    //because content page might have some user input.
    //So we remove it from current window and add to next. As a result we need to remove it during
    //onPause and add during on resume to insure that single instance is created.
    //Buttons are different: their state can be restored. So we can recreate them or create multiple buttons.
    //Buttons are created during onStart and removed during onStop so there would be no flickering
    //(Since it is not expected to navigate between activities in expanded mode there would be no flickering for
    //expandView as well).
    private final Map<Activity, PhialButton> buttons = new HashMap<>();
    private final DragHelper dragHelper;
    private final ExpandedView expandedView;
    private final ScreenTracker screenTracker;
    private final SelectedPageStorage selectedPageStorage;
    private final List<Page> pages;
    private final PhialNotifier notifier;
    private final Context context;

    private boolean isExpanded = false;
    private Activity curActivity;

    public OverlayPresenter(Context baseContext, List<Page> pages,
                            SharedPreferences sp,
                            ScreenTracker screenTracker,
                            PhialNotifier notifier) {
        this.context = new ContextThemeWrapper(baseContext, R.style.Theme_Phial);
        this.pages = pages;
        this.screenTracker = screenTracker;
        this.notifier = notifier;
        this.selectedPageStorage = new SelectedPageStorage(sp);
        this.expandedView = new ExpandedView(context, this);

        final int padding = baseContext.getResources().getDimensionPixelSize(R.dimen.phial_content_padding);
        final PositionStorage positionStorage = new PositionStorage(sp);
        this.dragHelper = new DragHelper(positionStorage, padding, padding);

        screenTracker.addListener(this);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (!isExpanded) {
            showButton(activity, false);
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
            moveToExpandedStateIfPossible(activity, false);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ObjectUtil.equals(activity, curActivity)) {
            if (isExpanded) {
                removeExpandedView(curActivity);
            }

            curActivity = null;
        }
    }

    private void showDebugWindow() {
        final Activity curActivity = this.curActivity;
        final PhialButton curButton = buttons.get(curActivity);

        final Runnable endAction = () -> {
            notifier.fireDebugWindowShown();
            removePhialButton(curActivity, curButton);
            moveToExpandedStateIfPossible(curActivity, true);
        };
        dragHelper.animateToDefaultPosition(curButton, endAction);
    }

    private void closeDebugWindow() {
        expandedView.destroyContentAnimated(() -> {
            if (curActivity != null) {
                notifier.fireDebugWindowHide();
                removeExpandedView(curActivity);
                isExpanded = false;
                showButton(curActivity, true);
            }
        });
    }

    private void moveToExpandedStateIfPossible(Activity activity, boolean animated) {
        final boolean canShow = setupExpandedPage(animated);
        if (canShow) {
            isExpanded = true;
            activity.getWindowManager().addView(expandedView, wrap(EXPANDED_VIEW_PARAMS));
        } else {
            isExpanded = false;
            showButton(activity, false);
        }
    }

    private boolean setupExpandedPage(boolean animated) {
        final List<Page> visiblePages = calcVisiblePages();
        if (visiblePages.isEmpty()) {
            expandedView.destroyContent();
            return false;
        }

        final Page selected = findSelected(visiblePages);
        expandedView.displayPages(visiblePages, selected, animated);
        return true;
    }

    @Override
    public void onPageSelected(Page page) {
        if (isExpanded) {
            expandedView.setSelected(page);
            selectedPageStorage.setSelectedPage(page.getId());
        }
    }

    @Override
    public void onScreenChanged(Screen screen) {
        if (isExpanded && curActivity != null) {
            final boolean canUpdatePage = setupExpandedPage(false);
            if (!canUpdatePage) {
                isExpanded = false;
                removeExpandedView(curActivity);
                showButton(curActivity, false);
            }
        }
    }

    @NonNull
    private List<Page> calcVisiblePages() {
        final List<Page> visiblePages = new ArrayList<>(pages.size());
        final Screen screen = screenTracker.getCurrentScreen();
        for (Page page : pages) {
            final boolean shouldShowPage = screen.matchesAny(page.getTargetScreens());
            if (shouldShowPage) {
                visiblePages.add(page);
            }
        }
        return visiblePages;
    }

    private Page findSelected(List<Page> visible) {
        final String selectedPageId = selectedPageStorage.getSelectedPage();
        for (Page page : visible) {
            if (ObjectUtil.equals(selectedPageId, page.getId())) {
                return page;
            }
        }
        return visible.get(0);
    }

    private void removeExpandedView(Activity activity) {
        activity.getWindowManager().removeView(expandedView);
    }

    private void showButton(Activity activity, boolean animated) {
        WindowManager windowManager = activity.getWindowManager();
        final PhialButton button = createButton();
        buttons.put(activity, button);
        final LayoutParams wrap = wrap(BUTTON_PARAMS);
        windowManager.addView(button, wrap);
        dragHelper.manage(windowManager, button);
        if (animated) {
            dragHelper.animateFromDefaultPosition(button, null);
        }
    }

    private void removePhialButton(Activity activity) {
        final PhialButton button = buttons.remove(activity);
        removePhialButton(activity, button);
    }

    private void removePhialButton(Activity activity, PhialButton button) {
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

    @Nullable
    @Override
    public View findViewById(int id) {
        final Screen currentScreen = screenTracker.getCurrentScreen();
        if (currentScreen == null)
            return null;
        return currentScreen.findTarget(id);
    }

    private static LayoutParams wrap(LayoutParams source) {
        LayoutParams lp = new LayoutParams();
        lp.copyFrom(source);
        return lp;
    }

    public void destroy() {
        screenTracker.removeListener(this);
        if (isExpanded) {
            closeDebugWindow();
        }
    }
}
