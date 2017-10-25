package com.mindcoders.phial;

import android.app.Application;

/**
 * Created by rost on 10/22/17.
 */

public final class PhialOverlay {

    public static PhialBuilder builder(Application application) {
        return new PhialBuilder(application);
    }

}
