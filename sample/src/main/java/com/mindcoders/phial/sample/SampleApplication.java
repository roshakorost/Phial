package com.mindcoders.phial.sample;

import android.app.Application;


public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationHook.onApplicationCreate(this);
    }

}
