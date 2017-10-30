package com.mindcoders.phial.sample;

import android.app.Application;


public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // If you don't wand to include Phial in release application you may use flavors or build types
        // to add Phial only to one of them.
        // Here is example how to add phial only to single flavor "qa"

        // 1.Create needed build flavors in build.gradle (see sample build.gradle)
        // 2.Add Phial dependencies only to flavor that uses Phial see sample build.gradle
        // 3.Create ApplicationHook with same interface in all flavors.
        //   E.g. we created:
        //   * src/prod/java/com/mindcoders/phial/sample/ApplicationHook.java    (empty)
        //   * src/qa/java/com/mindcoders/phial/sample/ApplicationHook.java      (with setup of Phial)
        ApplicationHook.onApplicationCreate(this);
    }

}
