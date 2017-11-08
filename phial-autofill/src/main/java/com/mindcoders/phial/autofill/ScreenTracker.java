package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;
import com.mindcoders.phial.keyvalue.Saver;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 11/3/17.
 */

class ScreenTracker extends SimpleActivityLifecycleCallbacks implements Saver {
    interface ScreenListener {
        void onScreenChanged(Screen screen);
    }

    private final Screen currentScreen = Screen.empty();
    private final Set<String> targetKeys;
    private final List<ScreenListener> listeners = new CopyOnWriteArrayList<>();

    ScreenTracker(Set<String> targetKeys) {
        this.targetKeys = Collections.unmodifiableSet(targetKeys);
    }

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
        currentScreen.setActivity(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        super.onActivityStopped(activity);
        if (ObjectUtil.equals(activity, currentScreen.getActivity())) {
            currentScreen.clearActivity();
        }
    }

    @Override
    public void save(String category, String key, String value) {
        final String compositeKey = TargetScreen.createKey(category, key);
        if (targetKeys.contains(compositeKey)) {
            currentScreen.saveKeyValue(compositeKey, value);
        }
    }

    @Override
    public void remove(String category, String key) {
        final String compositeKey = TargetScreen.createKey(category, key);
        if (targetKeys.contains(compositeKey)) {
            currentScreen.removeKey(compositeKey);
        }
    }
}
