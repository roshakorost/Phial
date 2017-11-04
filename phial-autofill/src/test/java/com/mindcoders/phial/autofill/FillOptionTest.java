package com.mindcoders.phial.autofill;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by rost on 11/3/17.
 */
public class FillOptionTest {
    @Test
    public void withIds() throws Exception {
        assertEquals(
                new FillOption("1", Arrays.asList("a", "b"), Arrays.asList(1, 2)),
                new FillOption("1", Arrays.asList("a", "b")).withIds(Arrays.asList(1, 2))
        );
    }

    @Test
    public void withIds_return_new_instance() throws Exception {
        final FillOption option = new FillOption("1", Collections.emptyList());
        assertFalse("Should create new instance", option == option.withIds(new ArrayList<>()));
    }
}