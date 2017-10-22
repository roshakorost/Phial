package com.mindcoders.phial.overlay;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public final class OverlayLifecycleListener implements Application.ActivityLifecycleCallbacks {

    private int activeActivityCount;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activeActivityCount++;
        toggleOverlay();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeActivityCount--;
        toggleOverlay();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void toggleOverlay() {
        if (activeActivityCount > 0) {

        }
    }

}
