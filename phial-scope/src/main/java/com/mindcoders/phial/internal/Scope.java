package com.mindcoders.phial.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by okorsun on 23.11.17.
 */

public class Scope {

    private final String scopeName;
    private final View   scopeView;

    Scope(String scopeName, View scopeView) {
        this.scopeName = scopeName;
        this.scopeView = scopeView;
    }

    public static Scope createScope(@NonNull String scopeName, @Nullable View view) {
        return new Scope(scopeName, view);
    }

    public @NonNull String getScopeName() {
        return scopeName;
    }

    public @Nullable View getScopeView() {
        return scopeView;
    }
}
