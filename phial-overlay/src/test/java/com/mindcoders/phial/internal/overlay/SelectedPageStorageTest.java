package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;

/**
 * Created by rost on 11/27/17.
 */
@RunWith(RobolectricTestRunner.class)
public class SelectedPageStorageTest {
    SelectedPageStorage pageStorage;

    @Before
    public void setUp() throws Exception {
        final SharedPreferences sp = createSP();
        pageStorage = new SelectedPageStorage(sp);
    }

    private SharedPreferences createSP() {
        return RuntimeEnvironment.application
                .getSharedPreferences("SelectedPageStorage", Context.MODE_PRIVATE);
    }

    @Test
    public void getSelectedPage_returns_null_if_nothing_stored() {
        assertNull(pageStorage.getSelectedPage());
    }

    @Test
    public void getSelectedPage_returns_last_value() {
        pageStorage.setSelectedPage("1");
        assertEquals("1", pageStorage.getSelectedPage());
    }

    @Test
    public void setSelectedPage_persists_value() {
        pageStorage.setSelectedPage("2");
        final SelectedPageStorage newStorage = new SelectedPageStorage(createSP());
        assertEquals("2", newStorage.getSelectedPage());
    }

}