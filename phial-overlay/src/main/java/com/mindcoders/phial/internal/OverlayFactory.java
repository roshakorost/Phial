package com.mindcoders.phial.internal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.R;
import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.keyvalue.KeyValueView;
import com.mindcoders.phial.internal.overlay.Overlay;
import com.mindcoders.phial.internal.overlay.OverlayPositionStorage;
import com.mindcoders.phial.internal.overlay.SelectedPageStorage;
import com.mindcoders.phial.internal.overlay2.OverlayPresenter;
import com.mindcoders.phial.internal.overlay2.PositionStorage;
import com.mindcoders.phial.internal.share.ShareView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mindcoders.phial.internal.InternalPhialConfig.PREFERENCES_FILE_NAME;

/**
 * Created by rost on 10/25/17.
 */

public final class OverlayFactory {

    private OverlayFactory() {
        //to hide
    }


    public static OverlayPresenter createOverlay2(PhialBuilder phialBuilder, PhialCore phialCore) {
        final Application application = phialBuilder.getApplication();
        final List<Page> pages = createPages(phialBuilder, phialCore, application);

        return new OverlayPresenter(application,
                pages,
                phialCore.getSharedPreferences(),
                phialCore.getScreenTracker(),
                phialCore.getNotifier()
        );
    }

    public static Overlay createOverlay(PhialBuilder phialBuilder, PhialCore phialCore) {
        final Application application = phialBuilder.getApplication();
        final List<Page> pages = createPages(phialBuilder, phialCore, application);

        final SharedPreferences prefs = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        final OverlayPositionStorage positionStorage = new OverlayPositionStorage(prefs);
        final SelectedPageStorage selectedPageStorage = new SelectedPageStorage(prefs);

        return new Overlay(
                application,
                pages, phialCore.getNotifier(),
                phialCore.getActivityProvider(),
                phialCore.getScreenTracker(),
                positionStorage,
                selectedPageStorage
        );
    }

    @NonNull
    private static List<Page> createPages(PhialBuilder phialBuilder, PhialCore phialCore, Application application) {
        final List<Page> pages = new ArrayList<>();

        if (phialBuilder.enableKeyValueView()) {
            final Page page = new Page(
                    "keyvalue",
                    R.drawable.ic_keyvalue,
                    application.getString(R.string.system_info_page_title),
                    new KVPageFactory(phialCore),
                    Collections.<TargetScreen>emptySet()
            );
            pages.add(page);
        }

        if (phialBuilder.enableShareView()) {
            final Page page = new Page(
                    "share",
                    R.drawable.ic_share,
                    application.getString(R.string.share_page_title),
                    new ShareViewFactory(phialCore),
                    Collections.<TargetScreen>emptySet()
            );
            pages.add(page);
        }

        pages.addAll(phialBuilder.getPages());
        return pages;
    }

    private static final class KVPageFactory implements Page.PageViewFactory<KeyValueView> {
        private final PhialCore phialCore;

        KVPageFactory(PhialCore phialCore) {
            this.phialCore = phialCore;
        }

        @Override
        public KeyValueView createPageView(Context context, OverlayCallback overlayCallback) {
            return new KeyValueView(context, phialCore.getKvSaver());
        }
    }

    private static final class ShareViewFactory implements Page.PageViewFactory<ShareView> {
        private final PhialCore phialCore;

        ShareViewFactory(PhialCore phialCore) {
            this.phialCore = phialCore;
        }

        @Override
        public ShareView createPageView(Context context, OverlayCallback overlayCallback) {
            return new ShareView(context, phialCore.getShareManager(), phialCore.getAttachmentManager(), overlayCallback);
        }
    }
}
