package com.mindcoders.phial.overlay;

import com.mindcoders.phial.R;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

class OverlayView extends LinearLayout {

    interface OnPageSelectedListener {

        void onFirstPageSelected(Page page);

        void onPageSelectionChanged(Page page);

        void onNothingSelected();

    }

    private final List<Page> pages = new ArrayList<>();

    private final ImageButton mainButton;

    OnPageSelectedListener onPageSelectedListener;

    private boolean isShown;

    private Page selectedPage;

    public OverlayView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);

        mainButton = new ImageButton(context);
        mainButton.setImageResource(R.drawable.ic_handle);
        mainButton.setBackgroundResource(R.drawable.bg_overlay_button);
        mainButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        LinearLayout.LayoutParams params = new LayoutParams(160, 160);
        addView(mainButton, params);
    }

    public void addPage(Page page) {
        pages.add(page);
        addPageButton(page, 0);
    }

    public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
        this.onPageSelectedListener = onPageSelectedListener;
    }

    public void setOnHandleTouchListener(OnTouchListener onTouchListener) {
        mainButton.setOnTouchListener(onTouchListener);
    }

    void toggle() {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View v = getChildAt(i);
            v.setVisibility(isShown ? View.GONE : View.VISIBLE);
        }
        isShown = !isShown;
    }

    private void addPageButton(final Page page, int position) {
        ImageButton button = new ImageButton(getContext());
        button.setImageResource(page.iconResourceId);
        button.setBackgroundResource(R.drawable.bg_overlay_button);
        LinearLayout.LayoutParams params = new LayoutParams(160, 160);
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

    static class Page {

        final int iconResourceId;

        final PageViewFactory pageViewFactory;

        Page(int iconResourceId, PageViewFactory pageViewFactory) {
            this.iconResourceId = iconResourceId;
            this.pageViewFactory = pageViewFactory;
        }

    }

}
