package com.mindcoders.phial;

import android.content.Context;
import android.view.View;

public final class Page {

    public interface PageViewFactory<T extends View & PageView> {

        T createPageView(Context context, OverlayCallback overlayCallback);

    }

    private final String id;
    private final int iconResourceId;
    private final CharSequence title;
    private final PageViewFactory pageViewFactory;

    public Page(String id, int iconResourceId, String title, PageViewFactory pageViewFactory) {
        this.id = id;
        this.iconResourceId = iconResourceId;
        this.title = title;
        this.pageViewFactory = pageViewFactory;
    }

    public String getId() {
        return id;
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
