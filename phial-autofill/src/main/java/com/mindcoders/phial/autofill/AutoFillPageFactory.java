package com.mindcoders.phial.autofill;

import android.content.Context;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.ScreenTracker;

import java.util.List;

/**
 * Created by rost on 11/4/17.
 */

class AutoFillPageFactory implements Page.PageViewFactory<FillView> {

    private final List<FillConfig> configs;

    AutoFillPageFactory(List<FillConfig> configs) {
        this.configs = configs;
    }

    @Override
    public FillView createPageView(
            Context context,
            OverlayCallback overlayCallback,
            ScreenTracker screenTracker
    ) {
        return new FillView(context, configs, screenTracker, overlayCallback);
    }

}
