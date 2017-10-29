package com.mindcoders.phial;

import android.content.Context;
import android.view.View;

public final class Page {

    public interface PageViewFactory<T extends View & PageView> {

        T createPageView(Context context);

    }

    private final int iconResourceId;
    private final CharSequence title;
    private final PageViewFactory pageViewFactory;

    public Page(int iconResourceId, String title, PageViewFactory pageViewFactory) {
        this.iconResourceId = iconResourceId;
        this.title = title;
        this.pageViewFactory = pageViewFactory;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public CharSequence getTitle() {
        return title;
    }

    public PageViewFactory getPageViewFactory() {
        return pageViewFactory;
    }

}
