package com.mindcoders.phial;

import android.app.Application;

/**
 * Created by rost on 10/22/17.
 */

public final class Phial {

    public static PhialBuilder builder(Application application) {
        return new PhialBuilder(application);
    }

}
