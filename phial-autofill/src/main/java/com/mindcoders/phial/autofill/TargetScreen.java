package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.mindcoders.phial.internal.util.ObjectUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class is used to abstract from activity,
 * since in future we might provide functionality to show options for Fragment as well
 */
class TargetScreen {
    private final Class<? extends Activity> target;

    private final String key;
    private final String value;

    TargetScreen(Class<? extends Activity> target, String group, String key, String value) {
        this.target = target;
        this.key = createKey(group, key);
        this.value = value;
    }

    Class<? extends Activity> getTargetActivity() {
        return target;
    }

    String getTargetKey() {
        return key;
    }

    String getTargetValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TargetScreen that = (TargetScreen) o;

        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @NonNull
    static String createKey(String category, String key) {
        return category + ":" + key;
    }
}

class Screen {
    private Activity activity;
    private final Map<String, String> keyValues = new ConcurrentHashMap<>();

    Screen(Activity activity) {
        this.activity = activity;
    }

    static Screen empty() {
        return new Screen(null);
    }

    void saveKeyValue(String key, String value) {
        keyValues.put(key, value);
    }

    void removeKey(String key) {
        keyValues.remove(key);
    }

    void setActivity(Activity activity) {
        this.activity = activity;
    }

    void clearActivity() {
        this.activity = null;
    }

    boolean matches(TargetScreen screen) {
        if (activity == null) {
            return false;
        }

        final String targetKey = screen.getTargetKey();
        if (targetKey != null) {
            final boolean sameKeyValues = ObjectUtil.equals(screen.getTargetValue(), keyValues.get(targetKey));
            if (!sameKeyValues) {
                return false;
            }
        }

        if (screen.getTargetActivity() != null) {
            final boolean activityMatches = ObjectUtil.equals(screen.getTargetActivity(), activity.getClass());
            if (!activityMatches) {
                return false;
            }
        }

        return true;
    }

    Activity getActivity() {
        return activity;
    }

    View findTarget(int id) {
        if (activity != null) {
            return activity.findViewById(id);
        }
        return null;
    }
}

