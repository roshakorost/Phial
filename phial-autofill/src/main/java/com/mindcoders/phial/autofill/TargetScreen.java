/*
package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.view.View;

import com.mindcoders.phial.internal.util.ObjectUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

*/
/**
 * Class is used to abstract from activity,
 * since in future we might provide functionality to show options for Fragment as well
 *//*

class TargetScreen {
    private final Class<? extends Activity> target;
    private final String scope;


    TargetScreen(Class<? extends Activity> target, String scope) {
        this.target = target;
        this.scope = scope;
    }

    static TargetScreen forActivity(Class<? extends Activity> target) {
        return new TargetScreen(target, null);
    }

    static TargetScreen forScope(String scope) {
        return new TargetScreen(null, scope);
    }


    Class<? extends Activity> getTargetActivity() {
        return target;
    }

    String getScope() {
        return scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetScreen that = (TargetScreen) o;

        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        return scope != null ? scope.equals(that.scope) : that.scope == null;
    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;
    }
}

class Screen {
    private Activity activity;
    private Set<String> scopes = Collections.synchronizedSet(new HashSet<>());

    Screen(Activity activity, String scope) {
        this.activity = activity;
    }

    static Screen empty() {
        return new Screen(null, null);
    }

    void setActivity(Activity activity) {
        this.activity = activity;
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

    boolean matches(TargetScreen screen) {
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

    View findTarget(int id) {
        if (activity != null) {
            return activity.findViewById(id);
        }
        return null;
    }
}

*/
