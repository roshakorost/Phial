package com.mindcoders.phial;

import com.mindcoders.phial.internal.PhialScopeNotifier;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class PhialScope extends PhialScopeNotifier {
    public static void enterScope(String scope) {
        fireEnterScope(scope);
    }

    public static void exitScope(String scope) {
        fireExitScope(scope);
    }
}
