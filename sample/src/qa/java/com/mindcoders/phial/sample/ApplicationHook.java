package com.mindcoders.phial.sample;

import android.app.Application;

import com.mindcoders.phial.PhialOverlay;

final class ApplicationHook {
    static void onApplicationCreate(Application app) {
        PhialOverlay.builder(app).initPhial();
    }
}
