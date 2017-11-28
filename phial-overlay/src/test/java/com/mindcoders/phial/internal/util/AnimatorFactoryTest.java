package com.mindcoders.phial.internal.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rost on 11/28/17.
 */
public class AnimatorFactoryTest {
    @Test
    public void calRadius_returns_correct_radius() throws Exception {
        final int radius = AnimatorFactory.calcRadius(3, 4);
        assertEquals(5, radius);
    }
}