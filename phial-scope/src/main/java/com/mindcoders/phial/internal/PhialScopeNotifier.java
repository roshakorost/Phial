package com.mindcoders.phial.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 11/15/17.
 */

public class PhialScopeNotifier {
    interface OnScopeChangedListener {

        void onEnterScope(String scope);

        void onExitScope(String scope);

    }

    private static final List<OnScopeChangedListener> LISTENERS = new CopyOnWriteArrayList<>();

    static void addListener(OnScopeChangedListener listener) {
        LISTENERS.add(listener);
    }

    static void removeListener(OnScopeChangedListener listener) {
        LISTENERS.remove(listener);
    }

    protected static void fireEnterScope(String scope) {
        for (OnScopeChangedListener listener : LISTENERS) {
            listener.onEnterScope(scope);
        }
    }

    protected static void fireExitScope(String scope) {
        for (OnScopeChangedListener listener : LISTENERS) {
            listener.onExitScope(scope);
        }
    }
}
