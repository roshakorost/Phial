package com.mindcoders.phial.sample;

import com.mindcoders.phial.overlay.Overlay;

import android.app.Application;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Overlay overlay = new Overlay(this);
    }

}
