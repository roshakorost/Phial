package com.mindcoders.phial;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.DrawableRes;

import com.mindcoders.phial.internal.OverlayFactory;
import com.mindcoders.phial.internal.PhialCore;
import com.mindcoders.phial.internal.overlay.Overlay;

/**
 * Creates PhialOverlay button and pages
 * <p>
 * Use {@link PhialBuilder} to setup
 */
public final class PhialOverlay {
    @SuppressLint("StaticFieldLeak")
    private static PhialCore phialCore;
    @SuppressLint("StaticFieldLeak")
    private static Overlay overlay;

    /**
     * @param application your application
     * @return builder that configures overlays
     */
    public static PhialBuilder builder(Application application) {
        return new PhialBuilder(application);
    }

    /**
     * Creates an instance of {@link PhialPageBuilder}.
     *
     * @param id              unique id of the page.
     * @param iconResourceId  icon resource of the page
     * @param title           page title.
     * @param pageViewFactory factory to create page view.
     * @return instance of {@link PhialPageBuilder}.
     */
    public static PhialPageBuilder pageBuilder(String id,
                                               @DrawableRes int iconResourceId,
                                               CharSequence title,
                                               Page.PageViewFactory pageViewFactory) {
        return new PhialPageBuilder(id, iconResourceId, title, pageViewFactory);
    }

    static void init(PhialBuilder builder) {
        destroy();
        PhialOverlay.phialCore = PhialCore.create(builder);
        PhialOverlay.overlay = OverlayFactory.createOverlay(builder, phialCore);
        builder.getApplication().registerActivityLifecycleCallbacks(OverlayFactory.createOverlay2(builder, phialCore));
    }

    /**
     * removes overlay button and pages
     */
    public static void destroy() {
        if (phialCore != null) {
            phialCore.destroy();
        }

        if (overlay != null) {
            overlay.destroy();
        }
    }
}
