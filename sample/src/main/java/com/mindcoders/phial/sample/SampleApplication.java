package com.mindcoders.phial.sample;

import com.mindcoders.phial.Phial;

import android.app.Application;

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Phial.builder(this).initPhial();
    }

}
