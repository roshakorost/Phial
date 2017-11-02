package com.mindcoders.phial.internal.util;

import android.app.Activity;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 10/23/17.
 */

public class CurrentActivityProvider extends SimpleActivityLifecycleCallbacks {
    private Activity activity;
    private int activeActivityCount;
    private final List<AppStateListener> listeners = new CopyOnWriteArrayList<>();

    public interface AppStateListener {
        void onAppForeground();

        void onAppBackground();
    }

    @Override
    public void onActivityStarted(Activity activity) {
        super.onActivityStarted(activity);
        this.activity = activity;
        activeActivityCount++;
        if (activeActivityCount == 1) {
            for (AppStateListener listener : listeners) {
                listener.onAppForeground();
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        super.onActivityStopped(activity);
        if (activity == this.activity) {
            activity = null;
        }
        activeActivityCount--;
        if (activeActivityCount == 0) {
            for (AppStateListener listener : listeners) {
                listener.onAppBackground();
            }
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void addListener(AppStateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AppStateListener listener) {
        listeners.remove(listener);
    }
}
