package com.mindcoders.phial.autofill;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */
public class AutoFillerBuilderTest {

    private TargetScreen screen;
    private AutoFillerBuilder builder;

    @Before
    public void setUp() throws Exception {
        screen = TargetScreen.forScope("Test");
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
    public void withOptions_without_calling_fill_rises_exception() {
        builder.withOptions(Mockito.mock(FillOption.class));
    }

    @Test(expected = Exception.class)
    public void withOptions_whits_size_of_option_that_not_match_rises_exception() {
        final FillOption opt1 = new FillOption("A", Arrays.asList("1", "2"));
        final FillOption opt2 = new FillOption("B", Arrays.asList("2", "3", "4"));

        builder.fill(1, 2)
                .withOptions(opt1, opt2);
    }

    @Test
    public void withOptions_returns_correct_FillConfig() {
        final FillOption opt1 = new FillOption("A", Arrays.asList("1", "2"));
        final FillOption opt2 = new FillOption("B", Arrays.asList("2", "3"));

        final FillConfig actual = builder.fill(1, 2)
                .withOptions(opt1, opt2);

        final List<Integer> ids = Arrays.asList(1, 2);
        final FillConfig expected = new FillConfig(
                screen,
                Arrays.asList(opt1.withIds(ids), opt2.withIds(ids))
        );
        Assert.assertEquals("Bad FillConfig is build", expected, actual);
    }
}