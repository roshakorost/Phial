package com.mindcoders.phial.autofill;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by rost on 11/15/17.
 */
@RunWith(RobolectricTestRunner.class)
public class StoreTest {
    private Store store;

    @Before
    public void setUp() throws Exception {
        final SharedPreferences sp = RuntimeEnvironment.application.getSharedPreferences("test", Context.MODE_PRIVATE);
        sp.edit().clear().commit();
        store = new Store(sp);
    }

    @Test
    public void read_without_saves_returns_empty_list() throws Exception {
        assertEquals(Collections.emptyList(), store.read("t"));
    }

    @Test
    public void save_with_old_key_will_update_value() throws Exception {
        store.save("t", Collections.singletonList("1"));
        store.save("t", Collections.singletonList("2"));
        assertEquals(Collections.singletonList("2"), store.read("t"));
    }
}
