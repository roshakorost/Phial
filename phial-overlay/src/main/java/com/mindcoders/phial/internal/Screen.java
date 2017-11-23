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
    private Set<Scope> scopes = Collections.synchronizedSet(new HashSet<Scope>());

    Screen(Activity activity, String scope) {
        this.activity = activity;
    }

    static Screen empty() {
        return new Screen(null, null);
    }

    void enterScope(Scope scope) {
        this.scopes.add(scope);
    }

    void exitScope(Scope scope) {
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
        View resultView = null;

        for (Scope scope : scopes) {
            if (scope.getScopeView() != null) {
                resultView = scope.getScopeView().findViewById(id);
            }

            if (resultView != null) {
                return resultView;
            }
        }


        if (activity != null) {
            resultView = activity.findViewById(id);
        }
        return resultView;
    }
}
