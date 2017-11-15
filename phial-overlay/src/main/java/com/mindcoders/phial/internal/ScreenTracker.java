package com.mindcoders.phial.internal;

import android.app.Activity;

import com.mindcoders.phial.PhialScope;
import com.mindcoders.phial.internal.util.ObjectUtil;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 11/3/17.
 */

public final class ScreenTracker extends SimpleActivityLifecycleCallbacks implements PhialScopeNotifier.OnScopeChangedListener {

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

    @Override
    public void onActivityResumed(Activity activity) {
        currentScreen.setActivity(activity);
        fireOnScreenChanged();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ObjectUtil.equals(activity, currentScreen.getActivity())) {
            currentScreen.clearActivity();
            fireOnScreenChanged();
        }
    }

    private void fireOnScreenChanged() {
        for (ScreenListener listener : listeners) {
            listener.onScreenChanged(currentScreen);
        }
    }

    @Override
    public void onEnterScope(String scope) {
        currentScreen.enterScope(scope);
        fireOnScreenChanged();
    }

    @Override
    public void onExitScope(String scope) {
        currentScreen.exitScope(scope);
        fireOnScreenChanged();
    }
}
