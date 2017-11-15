package com.mindcoders.phial;

import android.app.Activity;

import com.mindcoders.phial.internal.util.ObjectUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 11/3/17.
 */

public final class ScreenTracker {

    public interface ScreenListener {

        void onScreenChanged(Screen screen);

    }

    private static final Screen currentScreen = Screen.empty();

    private static final List<ScreenListener> listeners = new CopyOnWriteArrayList<>();

    public static void addListener(ScreenListener listener) {
        listeners.add(listener);
        fireOnScreenChanged();
    }

    public static void removeListener(ScreenListener listener) {
        listeners.remove(listener);
    }

    public static Screen getCurrentScreen() {
        return currentScreen;
    }

    public static void onActivityResumed(Activity activity) {
        currentScreen.setActivity(activity);
        fireOnScreenChanged();
    }

    public static void onActivityPaused(Activity activity) {
        if (ObjectUtil.equals(activity, currentScreen.getActivity())) {
            currentScreen.clearActivity();
            fireOnScreenChanged();
        }
    }

    public static void enterScope(String scope) {
        currentScreen.enterScope(scope);
        fireOnScreenChanged();
    }

    public static void exitScope(String scope) {
        currentScreen.exitScope(scope);
        fireOnScreenChanged();
    }

    private static void fireOnScreenChanged() {
        for (ScreenListener listener : listeners) {
            listener.onScreenChanged(currentScreen);
        }
    }

}
