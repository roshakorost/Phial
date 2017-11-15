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

    /**
     * Returns activity that is currently resumed and visible.
     */
    Activity getCurrentActivity();

}
