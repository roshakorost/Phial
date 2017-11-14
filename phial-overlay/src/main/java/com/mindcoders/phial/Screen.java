package com.mindcoders.phial;

import android.app.Activity;
import android.view.View;

import com.mindcoders.phial.internal.util.ObjectUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Screen {

    private Activity activity;
    private Set<String> scopes = Collections.synchronizedSet(new HashSet<String>());

    Screen(Activity activity, String scope) {
        this.activity = activity;
    }

    static Screen empty() {
        return new Screen(null, null);
    }

    void enterScope(String scope) {
        this.scopes.add(scope);
    }

    void exitScope(String scope) {
        this.scopes.remove(scope);
    }

    void clearActivity() {
        this.activity = null;
    }

    public boolean matches(TargetScreen screen) {
        if (activity == null) {
            return false;
        }

        if (screen.getScope() != null) {
            final boolean sameScope = scopes.contains(screen.getScope());
            if (!sameScope) {
                return false;
            }
        }

        if (screen.getTargetActivity() != null) {
            final boolean activityMatches = ObjectUtil.equals(screen.getTargetActivity(), activity.getClass());
            if (!activityMatches) {
                return false;
            }
        }

        return true;
    }

    Activity getActivity() {
        return activity;
    }

    void setActivity(Activity activity) {
        this.activity = activity;
    }

    public View findTarget(int id) {
        if (activity != null) {
            return activity.findViewById(id);
        }
        return null;
    }
}
