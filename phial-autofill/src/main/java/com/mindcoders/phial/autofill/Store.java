package com.mindcoders.phial.autofill;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by rost on 11/15/17.
 */
class Store {
    private final SharedPreferences sharedPreferences;

    Store(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    static Store create(Context context, String key) {
        final SharedPreferences sp = context.getSharedPreferences("AutoFiller_" + key, Context.MODE_PRIVATE);
        return new Store(sp);
    }

    void save(String name, List<String> values) {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < values.size(); i++) {
            final String nameWithIndex = String.format(Locale.US, "%03d_%s", i, name);
            editor.putString(nameWithIndex, values.get(i));
        }
        editor.apply();
    }

    List<String> read(String name) {
        final List<String> allKeys = new ArrayList<>(sharedPreferences.getAll().keySet());
        Collections.sort(allKeys);
        final Pattern pattern = Pattern.compile("^[0-9]+" + Pattern.quote("_" + name) + "$");

        final List<String> result = new ArrayList<>();
        for (String key : allKeys) {
            if (pattern.matcher(key).matches()) {
                result.add(sharedPreferences.getString(key, AutoFiller.EMPTY_FIELD));
            }
        }
        return result;
    }
}
