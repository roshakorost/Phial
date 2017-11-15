package com.mindcoders.phial.internal.keyvalue;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.mindcoders.phial.internal.keyvalue.KVJsonSerializer.ENTRIES_KEY;
import static com.mindcoders.phial.internal.keyvalue.KVJsonSerializer.NAME_KEY;
import static com.mindcoders.phial.internal.keyvalue.KVJsonSerializer.VALUE_KEY;
import static org.junit.Assert.assertEquals;

/**
 * Created by rost on 11/14/17.
 */
// need to work with jsonObject
@RunWith(RobolectricTestRunner.class)
public class KVJsonSerializerTest {
    private KVJsonSerializer serializer;

    @Before
    public void setUp() throws Exception {
        serializer = new KVJsonSerializer();
    }

    @Test
    public void createCategoryObject() throws Exception {
        final KVSaver.KVCategory testCategory = new KVSaver.KVCategory("cat", Arrays.asList(
                new KVSaver.KVEntry("k1", "v1"),
                new KVSaver.KVEntry("k2", "v2")
        ));
        final JSONObject category = serializer.createCategoryObject(testCategory);

        assertEquals("cat", category.getString(NAME_KEY));

        final JSONArray entries = category.getJSONArray(ENTRIES_KEY);
        assertEquals(2, entries.length());
        assertEquals("k1", entries.getJSONObject(0).getString(NAME_KEY));
        assertEquals("v1", entries.getJSONObject(0).getString(VALUE_KEY));
        assertEquals("k2", entries.getJSONObject(1).getString(NAME_KEY));
        assertEquals("v2", entries.getJSONObject(1).getString(VALUE_KEY));
    }

    @Test
    public void createCategoriesArray_returns_empty_array_for_empty_collection() throws Exception {
        final JSONArray array = serializer.createCategoriesArray(Collections.emptyList());
        assertEquals(0, array.length());
    }

    @Test
    public void createCategoriesArray_returns_correct_json_object() throws Exception {
        final List<KVSaver.KVCategory> testCategories = Arrays.asList(
                new KVSaver.KVCategory("cat", Collections.singletonList(
                        new KVSaver.KVEntry("k1", "v1"))),
                new KVSaver.KVCategory("cat", Collections.emptyList())
        );
        final JSONArray categories = serializer.createCategoriesArray(testCategories);
        assertEquals(1, categories.length());
        assertEquals("cat", categories.getJSONObject(0).getString(NAME_KEY));
    }
}