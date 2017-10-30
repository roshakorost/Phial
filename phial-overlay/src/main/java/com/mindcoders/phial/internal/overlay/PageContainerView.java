package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mindcoders.phial.PageView;
import com.mindcoders.phial.R;

import static com.mindcoders.phial.internal.util.UiUtils.dpToPx;

final class PageContainerView extends LinearLayout {

    private final TextView pageTitleTextView;
    private final FrameLayout pageContainer;

    private View currentPageView;

    public PageContainerView(Context context) {
        super(context);
        setOrientation(VERTICAL);

        pageTitleTextView = new TextView(context);
        pageContainer = new FrameLayout(context);

        pageTitleTextView.setTextSize(18f);
        pageTitleTextView.setTextColor(Color.BLACK);

        int leftPadding = dpToPx(context, 16);
        int topBottomPadding = dpToPx(context, 8);

        pageTitleTextView.setPadding(leftPadding, topBottomPadding, leftPadding, topBottomPadding);
        pageTitleTextView.setBackgroundResource(R.drawable.bg_page_title);
        pageTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        pageTitleTextView.setTextColor(getResources().getColor(R.color.phial_title_color));
        pageTitleTextView.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        LayoutParams pageTitleParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        addView(pageTitleTextView, pageTitleParams);

        LayoutParams pageContainerParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        addView(pageContainer, pageContainerParams);
    }

    public void showPage(View pageView) {
        currentPageView = pageView;
        pageContainer.removeAllViews();
        pageContainer.addView(pageView);
    }

    public void setPageTitle(CharSequence title) {
        pageTitleTextView.setText(title);
    }

    public boolean onBackPressed() {
        return currentPageView != null && ((PageView) currentPageView).onBackPressed();
    }

}
