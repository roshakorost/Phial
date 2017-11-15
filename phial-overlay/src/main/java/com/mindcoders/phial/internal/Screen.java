package com.mindcoders.phial.internal;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.ObjectUtil;

import java.util.Collection;
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

    public boolean matchesAny(Collection<TargetScreen> screens) {
        if (CollectionUtils.isNullOrEmpty(screens)) {
            return true;
        }

        for (TargetScreen targetScreen : screens) {
            if (matches(targetScreen)) {
                return true;
            }
        }

        return false;
    }

    Activity getActivity() {
        return activity;
    }

    void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    public View findTarget(int id) {
        if (activity != null) {
            return activity.findViewById(id);
        }
        return null;
    }
}
