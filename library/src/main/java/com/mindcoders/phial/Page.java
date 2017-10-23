package com.mindcoders.phial;

import android.content.Context;
import android.view.View;

public final class Page {

    public interface PageViewFactory<T extends View> {

        T createPageView(Context context);

    }

    private final int iconResourceId;

    private final PageViewFactory pageViewFactory;

    public Page(int iconResourceId, PageViewFactory pageViewFactory) {
        this.iconResourceId = iconResourceId;
        this.pageViewFactory = pageViewFactory;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public PageViewFactory getPageViewFactory() {
        return pageViewFactory;
    }

}
