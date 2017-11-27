package com.mindcoders.phial.internal.overlay;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.Screen;
import com.mindcoders.phial.internal.ScreenTracker;
import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rost on 11/20/17.
 */

public class OverlayPresenter extends SimpleActivityLifecycleCallbacks implements ScreenTracker.ScreenListener {
    private final OverlayView view;
    private final List<Page> allPages;
    private final ScreenTracker screenTracker;
    private final SelectedPageStorage selectedPageStorage;
    private final PhialNotifier notifier;

    private boolean isExpanded = false;
    private Activity curActivity;

    public OverlayPresenter(
            OverlayView view,
            List<Page> allPages,
            SelectedPageStorage selectedPageStorage,
            ScreenTracker screenTracker,
            PhialNotifier notifier) {
        this.view = view;
        this.allPages = allPages;
        this.screenTracker = screenTracker;
        this.notifier = notifier;
        this.selectedPageStorage = selectedPageStorage;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (!isExpanded) {
            view.showButton(activity, false);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (!isExpanded) {
            view.removeButton(activity);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        curActivity = activity;
        if (isExpanded) {
            final List<Page> visiblePages = calcVisiblePages();
            if (!visiblePages.isEmpty()) {
                view.showExpandedView(activity, visiblePages, findSelected(visiblePages), false);
                return;
            }

            isExpanded = false;
            view.showButton(activity, false);
            view.removeExpandedView(activity, true);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ObjectUtil.equals(activity, curActivity)) {
            if (isExpanded) {
                view.removeExpandedView(curActivity, false);
            }

            curActivity = null;
        }
    }

    void showDebugWindow() {
        view.animateButtonToCorner(curActivity);
    }

    void onButtonMoved() {
        final List<Page> visiblePages = calcVisiblePages();
        if (!visiblePages.isEmpty()) {
            notifier.fireDebugWindowShown();
            isExpanded = true;

            if (curActivity != null) {
                view.removeButton(curActivity);
                view.showExpandedView(curActivity, visiblePages, findSelected(visiblePages), true);
            }
        }
    }


    void closeDebugWindow() {
        if (isExpanded) {
            view.animateHideExpandedView();
        }
    }

    void onExpandedViewHidden() {
        notifier.fireDebugWindowHide();
        isExpanded = false;

        if (curActivity != null) {
            view.removeExpandedView(curActivity, true);
            view.showButton(curActivity, true);
        }
    }

    void onPageSelected(Page page) {
        if (isExpanded) {
            view.setSelectedPage(page);
            selectedPageStorage.setSelectedPage(page.getId());
        }
    }

    @Override
    public void onScreenChanged(Screen screen) {
        if (isExpanded && curActivity != null) {
            final List<Page> visiblePages = calcVisiblePages();
            if (!visiblePages.isEmpty()) {
                view.updateExpandedView(visiblePages, findSelected(visiblePages));
                return;
            }
            isExpanded = false;
            view.removeExpandedView(curActivity, true);
            view.showButton(curActivity, false);
        }
    }

    @NonNull
    private List<Page> calcVisiblePages() {
        final List<Page> visiblePages = new ArrayList<>(allPages.size());
        final Screen screen = screenTracker.getCurrentScreen();
        for (Page page : allPages) {
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

    @Nullable
    public View findViewById(int id) {
        final Screen currentScreen = screenTracker.getCurrentScreen();
        if (currentScreen == null)
            return null;
        return currentScreen.findTarget(id);
    }

    public void destroy() {
        if (isExpanded) {
            closeDebugWindow();
        }
    }
}
