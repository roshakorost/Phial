package com.mindcoders.phial;

import android.content.Context;
import android.view.View;

/**
 * Phial allows adding custom pages as tabs using {@link PhialBuilder#addPage(Page)}
 * Description of page
 * <p>
 * Page should have title, icon and factory that will create view when page is selected
 */
public final class Page {

    /**
     * Factory that is used to create views when page is selected
     *
     * @param <T> view to display
     */
    public interface PageViewFactory<T extends View & PageView> {
        /**
         * @param context         android application context
         * @param overlayCallback interface for communication with Phial
         * @return view to display
         */
        T createPageView(Context context, OverlayCallback overlayCallback);
    }

    private final String id;
    private final int iconResourceId;
    private final CharSequence title;
    private final PageViewFactory pageViewFactory;

    /**
     * @param id              unique pageId
     * @param iconResourceId  page icon
     * @param title           page title
     * @param pageViewFactory factory that will create view when page is selected
     */
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
