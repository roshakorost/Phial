package com.mindcoders.phial.internal.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Subclasses may override only some methods from  Application.ActivityLifecycleCallbacks
 */
public class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //optional
    }

    @Override
    public void onActivityStarted(Activity activity) {
        //optional
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //optional
    }

    @Override
    public void onActivityPaused(Activity activity) {
        //optional
    }

    @Override
    public void onActivityStopped(Activity activity) {
        //optional
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //optional
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        //optional
    }
}
