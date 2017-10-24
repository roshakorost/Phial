package com.mindcoders.phial.internal.overlay;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

// TODO: 10/23/17 add pages from either left or right depending on which edge the handle is sticking to
class OverlayView extends LinearLayout {

    interface OnPageSelectedListener {

        void onFirstPageSelected(Page page);

        void onPageSelectionChanged(Page page);

        void onNothingSelected();

    }

    interface OnHandleMoveListener {

        void onMoveStart(float x, float y);

        void onMove(float dx, float dy);

        void onMoveEnd();

    }

    private final int btnSize;

    private final List<Page> pages = new ArrayList<>();

    private OnPageSelectedListener onPageSelectedListener;

    private OnHandleMoveListener onHandleMoveListener;

    /**
     * True if page buttons are shown
     */
    private boolean isExpanded;

    private Page selectedPage;

    public OverlayView(Context context, int btnSize) {
        super(context);
        this.btnSize = btnSize;
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

    private void toggle() {
        if (selectedPage != null) {
            selectedPage = null;
            onPageSelectedListener.onNothingSelected();
            setPageButtonsVisible(false);
        } else {
            setPageButtonsVisible(!isExpanded);
        }

        isExpanded = !isExpanded;
    }

    private void setPageButtonsVisible(boolean visible) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View v = getChildAt(i);
            v.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    private void addPageButton(final Page page, int position) {
        ImageButton button = new ImageButton(getContext());
        button.setImageResource(page.getIconResourceId());
        button.setBackgroundResource(R.drawable.bg_overlay_button);
        LinearLayout.LayoutParams params = new LayoutParams(btnSize, btnSize);
        addView(button, position, params);
        button.setVisibility(View.GONE);

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPageSelectedListener != null) {
                    if (selectedPage == null) {
                        selectedPage = page;
                        onPageSelectedListener.onFirstPageSelected(page);
                    } else if (selectedPage != page) {
                        selectedPage = page;
                        onPageSelectedListener.onPageSelectionChanged(page);
                    } else {
                        selectedPage = null;
                        onPageSelectedListener.onNothingSelected();
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
