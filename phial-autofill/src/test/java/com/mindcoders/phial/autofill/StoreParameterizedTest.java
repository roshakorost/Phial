package com.mindcoders.phial.autofill;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by rost on 11/15/17.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class StoreParameterizedTest {
    @ParameterizedRobolectricTestRunner.Parameters(name = "Data to save and read = {0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Collections.emptyList()},
                {Collections.singletonList("")},
                {Collections.singletonList("val1")},
                {Arrays.asList("val1", "val2", "val3")},
                {IntStream.range(0, 101).mapToObj(String::valueOf).collect(Collectors.toList())}
        });
    }

    private final Store store;
    private final List<String> dataToSave;

    public StoreParameterizedTest(List<String> dataToSave) {
        this.dataToSave = dataToSave;
        final SharedPreferences sp = RuntimeEnvironment.application.getSharedPreferences("test", Context.MODE_PRIVATE);
        sp.edit().clear().commit();

        store = new Store(sp);
    }

    @Test
    public void save_and_read() throws Exception {
        store.save("test", dataToSave);
        assertEquals(dataToSave, store.read("test"));
    }
}