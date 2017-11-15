package com.mindcoders.phial;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PhialScope {

    private static final List<OnScopeChangedListener> LISTENERS = new CopyOnWriteArrayList<>();

    static void addListener(OnScopeChangedListener listener) {
        LISTENERS.add(listener);
    }

    static void removeListener(OnScopeChangedListener listener) {
        LISTENERS.remove(listener);
    }

    public static void enterScope(String scope) {
        for (OnScopeChangedListener listener : LISTENERS) {
            listener.onEnterScope(scope);
        }
    }

    public static void exitScope(String scope) {
        for (OnScopeChangedListener listener : LISTENERS) {
            listener.onExitScope(scope);
        }
    }

    interface OnScopeChangedListener {

        void onEnterScope(String scope);

        void onExitScope(String scope);

    }

}
