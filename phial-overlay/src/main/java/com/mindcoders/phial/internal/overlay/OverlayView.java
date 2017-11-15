package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.VisibleForTesting;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.Screen;
import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial.internal.util.ViewUtil;
import com.mindcoders.phial.internal.util.support.ResourcesCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class OverlayView extends LinearLayout {
    private static final long CLICK_MAX_DURATION_MS = 300L;
    private final HandleButton btnHandle;

    interface OnPageSelectedListener {

        void onFirstPageSelected(Page page, int position);

        void onPageSelectionChanged(Page page, int position);

        void onNothingSelected();

    }

    interface OnHandleMoveListener {

        void onMoveStart(float x, float y);

        void onMove(float dx, float dy);

        void onMoveEnd();

    }

    private final int btnSize;

    private final SelectedPageStorage selectedPageStorage;

    private final List<Page> pages = new ArrayList<>();

    private final Map<Page, View> pageViewMap = new LinkedHashMap<>();

    private OnPageSelectedListener onPageSelectedListener;

    private OnHandleMoveListener onHandleMoveListener;

    /**
     * True if page buttons are shown
     */
    private boolean isExpanded;

    private Page selectedPage;

    @VisibleForTesting
    OverlayView(Context context) {
        super(context);
        Precondition.calledFromTools(this);
        btnSize = 64;
        btnHandle = createButton(R.drawable.ic_handle);
        selectedPageStorage = null;
    }

    OverlayView(
            Context context,
            int btnSize,
            SelectedPageStorage selectedPageStorage
    ) {
        super(context);
        this.btnSize = btnSize;
        this.selectedPageStorage = selectedPageStorage;
        setOrientation(HORIZONTAL);


        btnHandle = createButton(R.drawable.ic_handle);
        btnHandle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        btnHandle.setOnTouchListener(handleOnTouchListener);

        LinearLayout.LayoutParams params = new LayoutParams(btnSize, btnSize);
        addView(btnHandle, params);
    }

    public void updateVisiblePages(Screen screen) {
        for (Page page : pages) {
            final boolean shouldShowPage = screen.matchesAny(page.getTargetScreens());
            if (shouldShowPage && !pageViewMap.containsKey(page)) {
                addPageButton(page);
            } else if (!shouldShowPage && pageViewMap.containsKey(page)) {
                removeView(pageViewMap.remove(page));
            }
        }

        updateSelection();
    }

    private void updateSelection() {
        Page previouslySelectedPage = getPreviouslySelectedPage();
        if (previouslySelectedPage != null && !pageViewMap.containsKey(previouslySelectedPage)) {
            Iterator<Page> iterator = pageViewMap.keySet().iterator();
            if (iterator.hasNext()) {
                Page page = iterator.next();
                selectedPageStorage.setSelectedPage(page.getId());
            }
        }
    }

    public void addPages(List<Page> pages, Screen screen) {
        this.pages.addAll(pages);
        for (Page page : this.pages) {
            addPageButton(page);
        }
        updateVisiblePages(screen);
    }

    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        this.onPageSelectedListener = onPageSelectedListener;
    }

    public void setOnHandleMoveListener(OnHandleMoveListener onHandleMoveListener) {
        this.onHandleMoveListener = onHandleMoveListener;
    }

    public void show() {
        if (pages.size() > 0 && !isExpanded) {
            isExpanded = true;
            setPageButtonsVisible(true);
            selectedPage = getPreviouslySelectedPage();
            onPageSelectedListener.onFirstPageSelected(selectedPage, pages.indexOf(selectedPage));
            setPageButtonsColors(true);
        }
    }

    public void hide() {
        if (pages.size() > 0 && isExpanded) {
            isExpanded = false;
            setPageButtonsVisible(false);
            selectedPage = null;
            onPageSelectedListener.onNothingSelected();
            setPageButtonsColors(false);
        }
    }

    private void toggle() {
        if (isExpanded) {
            hide();
        } else {
            show();
        }
    }

    private void setPageButtonsVisible(boolean visible) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View v = getChildAt(i);
            v.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void setPageButtonsColors(boolean isExpanded) {
        if (isExpanded) {
            View activeButton = pageViewMap.get(selectedPage);
            for (int i = 0; i < getChildCount() - 1; i++) {
                View btn = getChildAt(i);
                btn.setSelected(activeButton == btn);
            }
        }
    }

    private Page getPreviouslySelectedPage() {
        String id = selectedPageStorage.getSelectedPage();
        for (Page page : pages) {
            if (page.getId().equals(id)) {
                return page;
            }
        }

        return pages.get(0);
    }

    private void addPageButton(final Page page) {
        final HandleButton button = createButton(page.getIconResourceId());

        LinearLayout.LayoutParams params = new LayoutParams(btnSize, btnSize);
        addView(button, 0, params);
        button.setVisibility(View.GONE);
        button.setTag(page.getId());

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPageSelectedListener != null) {
                    if (selectedPage == null) {
                        selectedPage = page;
                        onPageSelectedListener.onFirstPageSelected(page, pages.indexOf(page));
                    } else if (selectedPage != page) {
                        selectedPage = page;
                        onPageSelectedListener.onPageSelectionChanged(page, pages.indexOf(page));
                        selectedPageStorage.setSelectedPage(page.getId());
                        setPageButtonsColors(isExpanded);
                    } else {
                        selectedPage = null;
                        onPageSelectedListener.onNothingSelected();
                        setPageButtonsVisible(false);
                        isExpanded = false;
                    }
                }
            }
        });

        pageViewMap.put(page, button);
    }

    private HandleButton createButton(@DrawableRes int iconResId) {
        final int bgColor = ResourcesCompat.getColor(getResources(), R.color.phial_button_background, getContext().getTheme());
        final int fgColor = ResourcesCompat.getColor(getResources(), R.color.phial_button_foreground, getContext().getTheme());
        return new HandleButton(getContext(), iconResId, bgColor, fgColor);
    }

    private final OnTouchListener handleOnTouchListener = new OnTouchListener() {

        private float initialTouchX, initialTouchY;
        private long startTimeMS;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (selectedPage != null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    startTimeMS = event.getEventTime();
                    onHandleMoveListener.onMoveStart(initialTouchX, initialTouchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onHandleMoveListener.onMove(event.getRawX() - initialTouchX, event.getRawY() - initialTouchY);
                    break;
                case MotionEvent.ACTION_UP:
                    onHandleMoveListener.onMoveEnd();
                    final long downTimeMS = event.getEventTime() - startTimeMS;
                    final boolean wasClicked = downTimeMS < CLICK_MAX_DURATION_MS
                            && ViewUtil.distance(initialTouchX, initialTouchY, event.getRawX(), event.getRawY()) < btnSize / 2;
                    if (wasClicked) {
                        v.performClick();
                    }
                    break;
                default:
                    return true;
            }

            return true;
        }
    };
}
