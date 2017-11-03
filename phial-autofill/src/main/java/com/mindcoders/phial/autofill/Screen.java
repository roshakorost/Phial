package com.mindcoders.phial.autofill;

import android.app.Activity;

/**
 * Class is used to abstract from activity,
 * since in future we might provide functionality to show options for Fragment as well
 */
class Screen {
    private final Class<? extends Activity> target;

    private Screen(Class<? extends Activity> target) {
        this.target = target;
    }

    static Screen from(Class<? extends Activity> target) {
        return new Screen(target);
    }
}
