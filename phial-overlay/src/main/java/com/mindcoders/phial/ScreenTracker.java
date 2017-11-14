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

    private final Screen currentScreen = Screen.empty();

    private final List<ScreenListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(ScreenListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ScreenListener listener) {
        listeners.remove(listener);
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }

    public void onActivityResumed(Activity activity) {
        currentScreen.setActivity(activity);
        fireOnScreenChanged();
    }

    public void onActivityPaused(Activity activity) {
        if (ObjectUtil.equals(activity, currentScreen.getActivity())) {
            currentScreen.clearActivity();
            fireOnScreenChanged();
        }
    }

    public void enterScope(String scope) {
        currentScreen.enterScope(scope);
        fireOnScreenChanged();
    }

    public void exitScope(String scope) {
        currentScreen.exitScope(scope);
        fireOnScreenChanged();
    }

    private void fireOnScreenChanged() {
        for (ScreenListener listener : listeners) {
            listener.onScreenChanged(currentScreen);
        }
    }

}
