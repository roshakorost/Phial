package com.mindcoders.phial.internal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.keyvalue.KeyValueView;
import com.mindcoders.phial.internal.overlay.Overlay;
import com.mindcoders.phial.internal.overlay.OverlayPositionStorage;
import com.mindcoders.phial.internal.share.ShareView;

import java.util.ArrayList;
import java.util.List;

import static com.mindcoders.phial.internal.InternalPhialConfig.*;

/**
 * Created by rost on 10/25/17.
 */

public final class OverlayFactory {
    private OverlayFactory() {
        //to hide
    }

    public static Overlay createOverlay(PhialBuilder phialBuilder, PhialCore phialCore) {
        final Application application = phialBuilder.getApplication();
        final List<Page> pages = new ArrayList<>();

        if (phialBuilder.enableKeyValueView()) {
            final Page page = new Page(
                    R.drawable.ic_keyvalue,
                    application.getString(R.string.system_info_page_title),
                    new KVPageFactory(phialCore)
            );
            pages.add(page);
        }

        if (phialBuilder.enableShareView()) {
            final Page page = new Page(
                    R.drawable.ic_share,
                    application.getString(R.string.share_page_title),
                    new ShareViewFactory(phialCore)
            );
            pages.add(page);
        }

        pages.addAll(phialBuilder.getPages());

        final SharedPreferences prefs = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        final OverlayPositionStorage positionStorage = new OverlayPositionStorage(prefs);

        return new Overlay(application, pages, phialCore.getNotifier(), phialCore.getActivityProvider(), positionStorage);
    }

    private static final class KVPageFactory implements Page.PageViewFactory {
        private final PhialCore phialCore;

        KVPageFactory(PhialCore phialCore) {
            this.phialCore = phialCore;
        }

        @Override
        public KeyValueView createPageView(Context context) {
            return new KeyValueView(context, phialCore.getKvSaver());
        }
    }

    private static final class ShareViewFactory implements Page.PageViewFactory {
        private final PhialCore phialCore;

        ShareViewFactory(PhialCore phialCore) {
            this.phialCore = phialCore;
        }

        @Override
        public ShareView createPageView(Context context) {
            return new ShareView(context, phialCore.getShareManager(), phialCore.getAttachmentManager());
        }
    }
}
