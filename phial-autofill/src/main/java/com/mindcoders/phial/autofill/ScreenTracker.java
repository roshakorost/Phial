package com.mindcoders.phial.autofill;

import android.app.Activity;

import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 11/3/17.
 */

class ScreenTracker extends SimpleActivityLifecycleCallbacks {
    interface ScreenListener {
        void onScreenChanged(Screen screen);
    }

    private Screen currentScreen = Screen.empty();
    private final List<ScreenListener> listeners = new CopyOnWriteArrayList<>();

    void addListener(ScreenListener listener) {
        listeners.add(listener);
        listener.onScreenChanged(currentScreen);
    }

    void removeListener(ScreenListener listener) {
        listeners.remove(listener);
    }

    Screen getCurrentScreen() {
        return currentScreen;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        super.onActivityStarted(activity);
        currentScreen = Screen.from(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        super.onActivityStopped(activity);
        if (ObjectUtil.equals(activity, currentScreen.getActivity())) {
            currentScreen = Screen.empty();
        }
    }
}
