package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;

import java.util.ArrayList;
import java.util.List;

class OverlayView extends LinearLayout {

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

    private OnPageSelectedListener onPageSelectedListener;

    private OnHandleMoveListener onHandleMoveListener;

    /**
     * True if page buttons are shown
     */
    private boolean isExpanded;

    private Page selectedPage;

    public OverlayView(
            Context context,
            int btnSize,
            SelectedPageStorage selectedPageStorage
                      ) {
        super(context);
        this.btnSize = btnSize;
        this.selectedPageStorage = selectedPageStorage;
        setOrientation(HORIZONTAL);

        HandleButton btnHandle = new HandleButton(context, android.R.color.white, R.drawable.ic_handle);
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

    public void addPage(Page page) {
        pages.add(page);
        addPageButton(page, 0);
    }

    public void addPages(List<Page> pages) {
        this.pages.addAll(pages);
        for (Page page : this.pages) {
            addPageButton(page, 0);
        }
    }

    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        this.onPageSelectedListener = onPageSelectedListener;
    }

    public void setOnHandleMoveListener(OnHandleMoveListener onHandleMoveListener) {
        this.onHandleMoveListener = onHandleMoveListener;
    }

    public void toggle() {
        if (pages.size() > 0) {
            setPageButtonsVisible(!isExpanded);
            isExpanded = !isExpanded;
            if (isExpanded) {
                selectedPage = getPreviouslySelectedPage();
                onPageSelectedListener.onFirstPageSelected(selectedPage, pages.indexOf(selectedPage));
            } else {
                selectedPage = null;
                onPageSelectedListener.onNothingSelected();
            }
        }
    }

    private void setPageButtonsVisible(boolean visible) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View v = getChildAt(i);
            v.setVisibility(visible ? View.VISIBLE : View.GONE);
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

    private void addPageButton(final Page page, int position) {
        HandleButton button = new HandleButton(getContext(), android.R.color.white, page.getIconResourceId());
        LinearLayout.LayoutParams params = new LayoutParams(btnSize, btnSize);
        addView(button, position, params);
        button.setVisibility(View.GONE);

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
                    } else {
                        selectedPage = null;
                        onPageSelectedListener.onNothingSelected();
                        setPageButtonsVisible(false);
                        isExpanded = false;
                    }
                }
            }
        });
    }

    private final OnTouchListener handleOnTouchListener = new OnTouchListener() {

        private float initialTouchX, initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (selectedPage != null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();

                    onHandleMoveListener.onMoveStart(initialTouchX, initialTouchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    onHandleMoveListener.onMove(event.getRawX() - initialTouchX, event.getRawY() - initialTouchY);
                    break;
                case MotionEvent.ACTION_UP:
                    onHandleMoveListener.onMoveEnd();
                    break;
            }

            return false;
        }
    };

}
