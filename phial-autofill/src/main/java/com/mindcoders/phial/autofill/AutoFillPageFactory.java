package com.mindcoders.phial.autofill;

import android.content.Context;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.ScreenTracker;

/**
 * Created by rost on 11/4/17.
 */

class AutoFillPageFactory implements Page.PageViewFactory<FillView> {

    private final FillConfig config;

    AutoFillPageFactory(FillConfig config) {
        this.config = config;
    }

    @Override
    public FillView createPageView(
            Context context,
            OverlayCallback overlayCallback,
            ScreenTracker screenTracker
    ) {
        final Store store = Store.create(context, config.getScreen().getName());
        final ConfigManager manager = new ConfigManager(config, store);
        return new FillView(context, manager, overlayCallback);
    }

}
