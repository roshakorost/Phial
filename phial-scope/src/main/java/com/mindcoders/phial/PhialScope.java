package com.mindcoders.phial;

import com.mindcoders.phial.internal.PhialScopeNotifier;

public final class PhialScope extends PhialScopeNotifier {

    /**
     * Enter a new scope.
     * @param scope scopeName
     */
    public static void enterScope(String scope) {
        fireEnterScope(scope);
    }

    /**
     * Exit the existing scope.
     * @param scope scopeName
     */
    public static void exitScope(String scope) {
        fireExitScope(scope);
    }

}
