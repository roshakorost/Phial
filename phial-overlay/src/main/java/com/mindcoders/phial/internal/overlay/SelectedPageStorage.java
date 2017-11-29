package com.mindcoders.phial.internal.overlay;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

class SelectedPageStorage {

    private static final String KEY_SELECTED_PAGE_ID = "selected_page_id";

    private final SharedPreferences preferences;

    SelectedPageStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Nullable
    String getSelectedPage() {
        return preferences.getString(KEY_SELECTED_PAGE_ID, null);
    }

    void setSelectedPage(String id) {
        preferences.edit()
                   .putString(KEY_SELECTED_PAGE_ID, id)
                   .apply();
    }
}
