package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.view.View;

import com.mindcoders.phial.internal.util.ObjectUtil;

/**
 * Class is used to abstract from activity,
 * since in future we might provide functionality to show options for Fragment as well
 */
class TargetScreen {
    private final Class<? extends Activity> target;

    private TargetScreen(Class<? extends Activity> target) {
        this.target = target;
    }

    static TargetScreen from(Class<? extends Activity> target) {
        return new TargetScreen(target);
    }

    Class<? extends Activity> getTarget() {
        return target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetScreen screen = (TargetScreen) o;

        return target != null ? target.equals(screen.target) : screen.target == null;
    }

    @Override
    public int hashCode() {
        return target != null ? target.hashCode() : 0;
    }
}

class Screen {
    private final Activity activity;

    Screen(Activity activity) {
        this.activity = activity;
    }

    static Screen from(Activity activity) {
        return new Screen(activity);
    }

    static Screen empty() {
        return new Screen(null);
    }

    boolean matches(TargetScreen screen) {
        return activity != null
                && ObjectUtil.equals(screen.getTarget(), activity.getClass());

    }

    Activity getActivity() {
        return activity;
    }

    View findTarget(int id) {
        if (activity != null) {
            return activity.findViewById(id);
        }
        return null;
    }
}

