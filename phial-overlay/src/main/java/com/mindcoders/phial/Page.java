package com.mindcoders.phial;

import android.content.Context;
import android.view.View;

import com.mindcoders.phial.internal.ScreenTracker;

import java.util.Collections;
import java.util.Set;

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
    private final Set<TargetScreen> targetScreens;

    /**
     * @param id              unique pageId
     * @param iconResourceId  page icon
     * @param title           page title
     * @param pageViewFactory factory that will create view when page is selected
     */
    public Page(
            String id,
            int iconResourceId,
            CharSequence title,
            PageViewFactory pageViewFactory
    ) {
        this(id, iconResourceId, title, pageViewFactory, Collections.<TargetScreen>emptySet());
    }

    /**
     * @param id              unique pageId
     * @param iconResourceId  page icon
     * @param title           page title
     * @param pageViewFactory factory that will create view when page is selected
     * @param targetScreens   screens to present this page on. Page will be present on all screens if this is empty.
     */
    public Page(
            String id,
            int iconResourceId,
            CharSequence title,
            PageViewFactory pageViewFactory,
            Set<TargetScreen> targetScreens
    ) {
        this.id = id;
        this.iconResourceId = iconResourceId;
        this.title = title;
        this.pageViewFactory = pageViewFactory;
        this.targetScreens = Collections.unmodifiableSet(targetScreens);
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

    public Set<TargetScreen> getTargetScreens() {
        return targetScreens;
    }

}
