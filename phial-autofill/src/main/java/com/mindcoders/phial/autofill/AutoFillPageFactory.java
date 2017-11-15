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
        return new FillView(context, config, overlayCallback);
    }

}
