package com.mindcoders.phial.internal.keyvalue;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.mindcoders.phial.internal.keyvalue.KVSaver.KVCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

class KVJsonSerializer {
    @VisibleForTesting
    static final int INDENT_SPACES = 2;
    @VisibleForTesting
    static final String NAME_KEY = "name";
    @VisibleForTesting
    static final String VALUE_KEY = "value";
    @VisibleForTesting
    static final String ENTRIES_KEY = "entries";

    String serializeToString(List<KVCategory> categories) throws JSONException {
        final JSONArray categoriesJsonArray = createCategoriesArray(categories);
        return categoriesJsonArray.toString(INDENT_SPACES);
    }

    @VisibleForTesting
    @NonNull
    JSONArray createCategoriesArray(List<KVCategory> categories) throws JSONException {
        final JSONArray categoriesJsonArray = new JSONArray();
        for (KVCategory category : categories) {
            if (category.isEmpty()) {
                continue;
            }

            final JSONObject categoryJsonObject = createCategoryObject(category);
            categoriesJsonArray.put(categoryJsonObject);
        }
        return categoriesJsonArray;
    }

    @VisibleForTesting
    @NonNull
    JSONObject createCategoryObject(KVCategory category) throws JSONException {
        final JSONObject categoryJsonObject = new JSONObject();
        categoryJsonObject.put(NAME_KEY, category.getName());

        final JSONArray entriesJsonArray = new JSONArray();
        for (KVSaver.KVEntry entry : category.entries()) {
            final JSONObject entryJsonObject = new JSONObject();
            entryJsonObject.put(NAME_KEY, entry.getName());
            entryJsonObject.put(VALUE_KEY, entry.getValue());
            entriesJsonArray.put(entryJsonObject);
        }

        categoryJsonObject.put(ENTRIES_KEY, entriesJsonArray);
        return categoryJsonObject;
    }
}
