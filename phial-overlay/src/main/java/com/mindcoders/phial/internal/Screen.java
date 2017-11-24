package com.mindcoders.phial.internal;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.ObjectUtil;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Screen {

    private Activity activity;
    private Map<String, WeakReference<View>> scopes = Collections.synchronizedMap(new HashMap<String, WeakReference<View>>());

    Screen(Activity activity, String scopeName) {
        this.activity = activity;
    }

    static Screen empty() {
        return new Screen(null, null);
    }

    void enterScope(String scopeName, View view) {
        this.scopes.put(scopeName, new WeakReference<>(view));
    }

    void exitScope(String scopeName) {
        this.scopes.remove(scopeName);
    }

    void clearActivity() {
        this.activity = null;
    }

    public boolean matches(TargetScreen screen) {
        if (activity == null) {
            return false;
        }

        if (screen.getScopeName() != null) {
            final boolean sameScope = scopes.containsKey(screen.getScopeName());
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

        for (WeakReference<View> weakRefView : scopes.values()) {
            View scopeView = weakRefView.get();
            if (scopeView != null) {
                resultView = scopeView.findViewById(id);
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
