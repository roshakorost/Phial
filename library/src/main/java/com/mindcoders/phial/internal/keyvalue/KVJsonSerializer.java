package com.mindcoders.phial.internal.keyvalue;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

class KVJsonSerializer {
    String serializeToString(List<KVCategory> categories) throws JSONException {
        final JSONArray categoriesJsonArray = new JSONArray();
        for (KVCategory category : categories) {
            if (category.isEmpty()) {
                continue;
            }

            final JSONObject categoryJsonObject = createCategoryObject(category);
            categoriesJsonArray.put(categoryJsonObject);
        }
        return categoriesJsonArray.toString();
    }

    @NonNull
    private JSONObject createCategoryObject(KVCategory category) throws JSONException {
        final JSONObject categoryJsonObject = new JSONObject();
        categoryJsonObject.put("name", category.getName());

        final JSONArray entriesJsonArray = new JSONArray();
        for (KVEntry entry : category.entries()) {
            final JSONObject entryJsonObject = new JSONObject();
            entryJsonObject.put("name", entry.getName());
            entryJsonObject.put("value", entry.getValue());
            entriesJsonArray.put(entryJsonObject);
        }

        categoryJsonObject.put("entries", entriesJsonArray);
        return categoryJsonObject;
    }
}
