package com.mindcoders.phial.autofill;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */
public class AutoFillerBuilderTest {

    private TargetScreen screen;
    private AutoFillerBuilder builder;

    @Before
    public void setUp() throws Exception {
        screen = TargetScreen.from(null);
        builder = new AutoFillerBuilder(screen);
    }

    @Test(expected = Exception.class)
    public void empty_fill_rises_exception() {
        builder.fill();
    }

    @Test(expected = Exception.class)
    public void empty_options_raises_exception() {
        builder.withOptions();
    }

    @Test(expected = Exception.class)
    public void build_without_calling_fill_rises_exception() {
        builder.withOptions(Mockito.mock(FillOption.class));
        builder.build();
    }

    @Test(expected = Exception.class)
    public void build_without_calling_withOptions_rises_exception() {
        builder.fill(1, 2);
        builder.build();
    }

    @Test
    public void build_returns_correct_FillConfig() {
        final FillOption opt1 = new FillOption("1", Collections.emptyList());
        final FillOption opt2 = new FillOption("1", Collections.emptyList());

        final FillConfig actual = builder.fill(1, 2)
                .withOptions(opt1, opt2)
                .build();

        final List<Integer> ids = Arrays.asList(1, 2);
        final FillConfig expected = new FillConfig(
                screen,
                Arrays.asList(opt1.withIds(ids), opt2.withIds(ids))
        );
        Assert.assertEquals("Bad FillConfig is build", expected, actual);
    }

}