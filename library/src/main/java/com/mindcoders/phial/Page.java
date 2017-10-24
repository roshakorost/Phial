package com.mindcoders.phial;

import android.content.Context;
import android.view.View;

public final class Page {

    public interface PageViewFactory {

        View createPageView(Context context);

    }

    private final int iconResourceId;

    private final String title;

    private final PageViewFactory pageViewFactory;

    public Page(int iconResourceId, String title, PageViewFactory pageViewFactory) {
        this.iconResourceId = iconResourceId;
        this.title = title;
        this.pageViewFactory = pageViewFactory;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public String getTitle() {
        return title;
    }

    public PageViewFactory getPageViewFactory() {
        return pageViewFactory;
    }

}
