package com.mindcoders.phial.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by okorsun on 23.11.17.
 */

public class Scope {

    private final String        scopeName;
    private final WeakReference<View>   scopeView;

    Scope(String scopeName, View scopeView) {
        this.scopeName = scopeName;
        this.scopeView = new WeakReference<>(scopeView);
    }

    public static Scope createScope(@NonNull String scopeName, @Nullable View view) {
        return new Scope(scopeName, view);
    }

    public @NonNull String getScopeName() {
        return scopeName;
    }

    public @Nullable View getScopeView() {
        return scopeView.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Scope scope = (Scope) o;

        return scopeName.equals(scope.scopeName);
    }

    @Override
    public int hashCode() {
        return scopeName.hashCode();
    }
}
