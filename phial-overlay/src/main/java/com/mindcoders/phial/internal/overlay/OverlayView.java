package com.mindcoders.phial.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION;

/**
 * Manages interaction with window manager
 */
class OverlayView implements ExpandedView.ExpandedViewCallback {
    private static final int WINDOW_TYPE = TYPE_APPLICATION;
    private static final LayoutParams BUTTON_PARAMS = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT,
            WINDOW_TYPE,
            FLAG_NOT_FOCUSABLE | FLAG_DIM_BEHIND,
            PixelFormat.TRANSLUCENT
    );
    private static final LayoutParams EXPANDED_VIEW_PARAMS = new LayoutParams(MATCH_PARENT, MATCH_PARENT,
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
    private final ExpandedView expandedView;
    private final Map<Activity, View> buttons = new HashMap<>();
    private final DragHelper dragHelper;
    private final Context context;
    private OverlayPresenter presenter;

    OverlayView(Context context, DragHelper dragHelper, ExpandedView expandedView) {
        this.dragHelper = dragHelper;
        this.expandedView = expandedView;
        this.context = context;
        expandedView.setCallback(this);
    }

    void setPresenter(OverlayPresenter presenter) {
        this.presenter = presenter;
    }

    void showButton(Activity activity, boolean animated) {
        WindowManager windowManager = activity.getWindowManager();
        final View button = createButton();
        buttons.put(activity, button);
        final LayoutParams wrap = wrap(BUTTON_PARAMS);
        windowManager.addView(button, wrap);
        dragHelper.manage(windowManager, button);
        if (animated) {
            dragHelper.animateFromDefaultPosition(button, null);
        }
    }

    void removeButton(Activity activity) {
        final View button = buttons.remove(activity);

        if (button != null){
            dragHelper.unmanage(button);
            activity.getWindowManager().removeView(button);
        }
    }

    void showExpandedView(Activity activity, List<Page> pages, Page selected, boolean animated) {
        expandedView.displayPages(pages, selected, animated);
        activity.getWindowManager().addView(expandedView, wrap(EXPANDED_VIEW_PARAMS));
    }

    void updateExpandedView(List<Page> pages, Page selected) {
        expandedView.displayPages(pages, selected, false);
    }

    void removeExpandedView(Activity activity) {
        activity.getWindowManager().removeView(expandedView);
    }

    void animateHideExpandedView() {
        expandedView.destroyContentAnimated(presenter::onExpandedViewHidden);
    }

    void animateButtonToCorner(Activity curActivity) {
        final View curButton = buttons.get(curActivity);
        dragHelper.animateToCorner(curButton, presenter::onButtonMoved);
    }

    void setSelectedPage(Page page) {
        expandedView.setSelected(page);
    }

    @Override
    public void finish() {
        presenter.closeDebugWindow();
    }

    @Nullable
    @Override
    public View findViewById(int id) {
        return presenter.findViewById(id);
    }

    @Override
    public void onPageSelected(Page page) {
        presenter.onPageSelected(page);
    }

    private View createButton() {
        final PhialButton button = new PhialButton(context);
        button.setIcon(R.drawable.ic_handle);
        button.setOnClickListener(v -> presenter.showDebugWindow());
        return button;
    }

    private static LayoutParams wrap(LayoutParams source) {
        LayoutParams lp = new LayoutParams();
        lp.copyFrom(source);
        return lp;
    }

    void freeExpandedContent() {
        expandedView.destroyContent();
    }
}
