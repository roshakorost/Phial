package com.mindcoders.phial.sample;

import com.mindcoders.phial.PhialOverlay;
import com.mindcoders.phial.internal.PhialErrorPlugins;

import android.app.Application;
import android.util.Log;


public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PhialOverlay.builder(this)
             .initPhial();

        PhialErrorPlugins.setHandler(new PhialErrorPlugins.ErrorHandler() {
            @Override
            public void onError(Throwable throwable) {
                Log.w("Phial", throwable);
            }
        });
    }

}
