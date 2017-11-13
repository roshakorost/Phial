package com.mindcoders.phial.internal.keyvalue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by rost on 11/13/17.
 */
public class KVSaverTest {
    private KVSaver saver;

    @Before
    public void setUp() throws Exception {
        saver = new KVSaver();
    }

    @Test
    public void save_inserts_value() {
        saver.save("cat", "key", "val");
        final KVSaver.KVCategory expected = createSingleItemCategory("cat", "key", "val");

        assertEquals(Collections.singletonList(expected), saver.getData());
    }

    @Test
    public void save_null_value_inserts_null_value() {
        saver.save("cat", "key", null);
        final KVSaver.KVCategory expected = createSingleItemCategory("cat", "key", null);

        assertEquals(Collections.singletonList(expected), saver.getData());
    }

    @Test
    public void save_same_category_and_key_updates_value() {
        saver.save("cat", "key", "val");
        saver.save("cat", "key", "val1");

        final KVSaver.KVCategory expected = createSingleItemCategory("cat", "key", "val1");

        assertEquals(Collections.singletonList(expected), saver.getData());
    }

    @Test
    public void save_different_category_same_key_inserts_value() {
        saver.save("cat1", "key", "val");
        saver.save("cat2", "key", "val1");

        final KVSaver.KVCategory expected1 = createSingleItemCategory("cat1", "key", "val");
        final KVSaver.KVCategory expected2 = createSingleItemCategory("cat2", "key", "val1");

        assertEquals(Arrays.asList(expected1, expected2), saver.getData());
    }

    @Test
    public void save_same_category_different_key_inserts_value() {
        saver.save("cat", "key1", "val1");
        saver.save("cat", "key2", "val2");

        final KVSaver.KVCategory expected = new KVSaver.KVCategory("cat", Arrays.asList(
                new KVSaver.KVEntry("key1", "val1"),
                new KVSaver.KVEntry("key2", "val2")));

        assertEquals(Collections.singletonList(expected), saver.getData());
    }

    @Test
    public void get_data_when_empty_returns_empty_collection() {
        assertTrue("collection should be empty", saver.getData().isEmpty());
    }

    @Test
    public void remove_removes_key() {
        saver.save("cat", "key1", "val1");
        saver.save("cat", "key2", "val2");
        saver.remove("cat", "key1");

        final KVSaver.KVCategory expected = createSingleItemCategory("cat", "key2", "val2");
        assertEquals(Collections.singletonList(expected), saver.getData());
    }

    @Test
    public void remove_last_key_removes_category() {
        saver.save("cat", "key", "val");
        saver.remove("cat", "key");

        assertTrue("collection should be empty", saver.getData().isEmpty());
    }

    @Test
    public void remove_key_that_not_exist_does_nothing() {
        saver.save("cat", "key", "val");
        saver.remove("cat", "key1");

        final KVSaver.KVCategory expected = createSingleItemCategory("cat", "key", "val");
        assertEquals(Collections.singletonList(expected), saver.getData());
    }

    @Test
    public void remove_key_when_empty_does_nothing() {
        saver.remove("cat", "key1");
        assertTrue("collection should be empty", saver.getData().isEmpty());
    }

    @Test
    public void remove_category_removes_category() {
        saver.save("cat", "key1", "val1");
        saver.save("cat", "key2", "val2");
        saver.save("cat2", "key3", "val3");
        saver.remove("cat");

        final KVSaver.KVCategory expected = createSingleItemCategory("cat2", "key3", "val3");
        assertEquals(Collections.singletonList(expected), saver.getData());
    }

    @Test
    public void save_notifies_about_change() {
        final Observer observable = Mockito.mock(Observer.class);
        saver.addObserver(observable);
        saver.save("cat", "key1", "val1");

        Mockito.verify(observable).update(Mockito.eq(saver), Mockito.any());
    }

    @Test
    public void update_notifies_about_change() {
        final Observer observable = Mockito.mock(Observer.class);
        saver.save("cat", "key1", "val1");
        saver.addObserver(observable);
        saver.save("cat", "key1", "val2");

        Mockito.verify(observable).update(Mockito.eq(saver), Mockito.any());
    }

    @Test
    public void remove_key_notifies_about_change() {
        final Observer observable = Mockito.mock(Observer.class);
        saver.save("cat", "key1", "val1");
        saver.addObserver(observable);
        saver.remove("cat", "key1");

        Mockito.verify(observable).update(Mockito.eq(saver), Mockito.any());
    }

    @Test
    public void remove_category_notifies_about_change() {
        final Observer observable = Mockito.mock(Observer.class);
        saver.save("cat", "key1", "val1");
        saver.addObserver(observable);
        saver.remove("cat");

        Mockito.verify(observable).update(Mockito.eq(saver), Mockito.any());
    }

    private static KVSaver.KVCategory createSingleItemCategory(String category, String key, String value) {
        return new KVSaver.KVCategory(category, Collections.singletonList(new KVSaver.KVEntry(key, value)));
    }
}