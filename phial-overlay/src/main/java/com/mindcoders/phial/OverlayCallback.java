package com.mindcoders.phial;

import android.app.Activity;

/**
 * Interface that allows communicate between page and Phial
 */
public interface OverlayCallback {

    /**
     * Called to close phial debug window
     */
    void finish();

    Activity getCurrentActivity();
}
