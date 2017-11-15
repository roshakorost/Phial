package com.mindcoders.phial.internal.overlay;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public final class SelectedPageStorage {

    private static final String KEY_SELECTED_PAGE_ID = "selected_page_id";

    private final SharedPreferences preferences;

    public SelectedPageStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Nullable
    public String getSelectedPage() {
        return preferences.getString(KEY_SELECTED_PAGE_ID, null);
    }

    public void setSelectedPage(String id) {
        preferences.edit()
                   .putString(KEY_SELECTED_PAGE_ID, id)
                   .apply();
    }

    public void removeSelectedPageId() {
        preferences.edit()
                   .remove(KEY_SELECTED_PAGE_ID)
                   .apply();
    }

}
