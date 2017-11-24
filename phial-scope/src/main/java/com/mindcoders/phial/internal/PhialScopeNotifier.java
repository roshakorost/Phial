package com.mindcoders.phial.internal;

import android.view.View;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 11/15/17.
 */

public class PhialScopeNotifier {
    interface OnScopeChangedListener {

        void onEnterScope(Scope scope);

        void onExitScope(Scope scope);

    }

    private static final List<OnScopeChangedListener> LISTENERS = new CopyOnWriteArrayList<>();

    static void addListener(OnScopeChangedListener listener) {
        LISTENERS.add(listener);
    }

    static void removeListener(OnScopeChangedListener listener) {
        LISTENERS.remove(listener);
    }

    protected static void fireEnterScope(String scopeName, View view) {
        Scope scope = Scope.createScope(scopeName, view);
        for (OnScopeChangedListener listener : LISTENERS) {
            listener.onEnterScope(scope);
        }
    }

    protected static void fireExitScope(String scopeName) {
        Scope scope = Scope.createScope(scopeName, null);
        for (OnScopeChangedListener listener : LISTENERS) {
            listener.onExitScope(scope);
        }
    }
}
