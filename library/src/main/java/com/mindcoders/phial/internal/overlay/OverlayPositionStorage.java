package com.mindcoders.phial.internal.overlay;

import android.content.SharedPreferences;
import android.graphics.Point;

public class OverlayPositionStorage {

    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";

    private final SharedPreferences preferences;

    public OverlayPositionStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    Point getPosition() {
        return new Point(
                preferences.getInt(KEY_X, 0),
                preferences.getInt(KEY_Y, 0)
        );
    }

    void savePosition(Point position) {
        preferences.edit()
                   .putInt(KEY_X, position.x)
                   .putInt(KEY_Y, position.y)
                   .apply();
    }

}
