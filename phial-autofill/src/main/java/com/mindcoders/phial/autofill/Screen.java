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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Screen screen = (Screen) o;

        return target != null ? target.equals(screen.target) : screen.target == null;
    }

    @Override
    public int hashCode() {
        return target != null ? target.hashCode() : 0;
    }
}
