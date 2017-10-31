package com.mindcoders.phial;

import android.annotation.SuppressLint;
import android.app.Application;

import com.mindcoders.phial.internal.OverlayFactory;
import com.mindcoders.phial.internal.PhialCore;
import com.mindcoders.phial.internal.overlay.Overlay;

/**
 * Created by rost on 10/22/17.
 */

public final class PhialOverlay {
    @SuppressLint("StaticFieldLeak")
    private static PhialCore phialCore;
    @SuppressLint("StaticFieldLeak")
    private static Overlay overlay;

    public static PhialBuilder builder(Application application) {
        return new PhialBuilder(application);
    }

    static void init(PhialBuilder builder) {
        destroy();
        PhialOverlay.phialCore = PhialCore.create(builder);
        PhialOverlay.overlay = OverlayFactory.createOverlay(builder, phialCore);
    }

    public static void destroy() {
        if (phialCore != null) {
            phialCore.destroy();
        }

        if (overlay != null) {
            overlay.destroy();
        }
    }
}
