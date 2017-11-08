package com.mindcoders.phial.autofill;

import android.content.Context;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;

import java.util.List;

/**
 * Created by rost on 11/4/17.
 */

class AutoFillPageFactory implements Page.PageViewFactory<FillView> {
    private final List<FillConfig> configs;
    private final ScreenTracker screenTracker;


    AutoFillPageFactory(List<FillConfig> configs, ScreenTracker screenTracker) {
        this.configs = configs;
        this.screenTracker = screenTracker;
    }

    @Override
    public FillView createPageView(Context context, OverlayCallback overlayCallback) {
        return new FillView(context, configs, screenTracker, overlayCallback);
    }
}
