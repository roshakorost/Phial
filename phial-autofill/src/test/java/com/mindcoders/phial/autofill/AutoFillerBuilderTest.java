package com.mindcoders.phial.autofill;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by rost on 11/3/17.
 */
public class AutoFillerBuilderTest {

    private Screen screen;
    private AutoFillerBuilder builder;

    @Before
    public void setUp() throws Exception {
        screen = Screen.from(null);
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
        final FillOption fillOption = Mockito.mock(FillOption.class);
        final FillConfig fillConfig = builder.fill(1, 2)
                .withOptions(fillOption)
                .build();

        final FillConfig expected = new FillConfig(
                screen,
                Arrays.asList(1, 2),
                Collections.singletonList(fillOption)
        );

        Assert.assertEquals("Bad FillConfig is build", expected, fillConfig);
    }

}