package com.mindcoders.phial;

import android.view.View;

import com.mindcoders.phial.internal.PhialScopeNotifier;

public final class PhialScope extends PhialScopeNotifier {

    /**
     * Enter a new scope.
     * @param scopeName scopeName
     */
    public static void enterScope(String scopeName) {
        fireEnterScope(scopeName, null);
    }

    /**
     * Enter a new scope if scope is not in the activity {@link android.view.Window}.
     * For example, in case of {@link android.app.DialogFragment}.
     * @param scopeName scopeName
     * @param view view of current scope
     */
    public static void enterScope(String scopeName, View view) {
        fireEnterScope(scopeName, view);
    }

    /**
     * Exit the existing scope.
     * @param scopeName scopeName
     */
    public static void exitScope(String scopeName) {
        fireExitScope(scopeName);
    }

}
