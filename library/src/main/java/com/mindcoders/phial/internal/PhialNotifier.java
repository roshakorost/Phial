package com.mindcoders.phial.internal;

import com.mindcoders.phial.PhialListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by rost on 10/23/17.
 */

public class PhialNotifier {
    private final List<PhialListener> listeners = new CopyOnWriteArrayList<>();

    public void addListener(PhialListener listener) {
        listeners.add(listener);
    }

    public void removeListener(PhialListener listener) {
        listeners.remove(listener);
    }

    public void fireDebugWindowShown() {
        for (PhialListener listener : listeners) {
            listener.onDebugWindowShow();
        }
    }

    public void fireDebugWindowHide() {
        for (PhialListener listener : listeners) {
            listener.onDebugWindowHide();
        }
    }

    void destroy() {
        listeners.clear();
    }
}
