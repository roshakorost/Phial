package com.mindcoders.phial.internal.overlay;

import android.content.SharedPreferences;

class PositionStorage {

    private static final String KEY_X = "rel_x";
    private static final String KEY_Y = "rel_y";

    private final SharedPreferences preferences;

    PositionStorage(SharedPreferences preferences) {
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

        Position(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
