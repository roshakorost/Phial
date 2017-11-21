package com.mindcoders.phial.internal.overlay2;

import android.content.SharedPreferences;
import android.graphics.Point;

class PositionStorage {

    private static final String KEY_X = "rel_x";
    private static final String KEY_Y = "rel_y";

    private final SharedPreferences preferences;

    public PositionStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    Position getPosition(float defaultX, float defaultY) {
        return new Position(
                preferences.getFloat(KEY_X, defaultX),
                preferences.getFloat(KEY_Y, defaultY)
        );
    }

    void savePosition(float x, float y) {
        preferences.edit()
                .putFloat(KEY_X, x)
                .putFloat(KEY_Y, y)
                .apply();
    }

    static class Position {
        final float x;
        final float y;

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
