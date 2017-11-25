package com.mindcoders.phial;

import android.support.annotation.Nullable;
import android.view.View;

/**
 * Interface that allows communicate between page and Phial
 */
public interface OverlayCallback {

    /**
     * Called to close phial debug window
     */
    void finish();


    /**
     * Finds view in application view hierarchy
     *
     * @param id view id.
     * @return view or null if view is missing
     */
    @Nullable
    View findViewById(int id);
}
