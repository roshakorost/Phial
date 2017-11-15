package com.mindcoders.phial.autofill;

import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.util.CollectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by rost on 11/16/17.
 */
public class ConfigManagerTest {
    private Store store;
    private FillConfig config;
    private ConfigManager configManager;

    @Before
    public void setUp() throws Exception {
        store = mock(Store.class);
        final List<Integer> ids = CollectionUtils.asList(-2, -1);
        final List<FillOption> options = simpleOptions("opt1", "opt2");
        config = new FillConfig(mock(TargetScreen.class), options, ids);
        configManager = new ConfigManager(config, store);
    }

    @Test
    public void getOptions_returns_all_options() throws Exception {
        when(store.read(eq(ConfigManager.ORDER))).thenReturn(Collections.singletonList("n1"));
        when(store.read(eq(ConfigManager.KEY_PREFIX + "n1"))).thenReturn(Arrays.asList("v1", "v2"));

        final List<FillOption> expected = new ArrayList<>();
        expected.add(new FillOption("n1", Arrays.asList("v1", "v2")));
        expected.addAll(simpleOptions("opt1", "opt2"));

        assertEquals(expected, configManager.getOptions());
    }

    @Test
    public void getOptions_not_include_options_with_bad_size() throws Exception {
        when(store.read(eq(ConfigManager.ORDER))).thenReturn(Arrays.asList("n1", "n2"));
        when(store.read(eq(ConfigManager.KEY_PREFIX + "n1"))).thenReturn(Collections.singletonList("v1"));
        when(store.read(eq(ConfigManager.KEY_PREFIX + "n1"))).thenReturn(Arrays.asList("v1", "v2", "v3"));

        assertEquals(simpleOptions("opt1", "opt2"), configManager.getOptions());
    }

    @Test
    public void getOptions_with_empty_custom_options_returns_options_from_config_only() throws Exception {
        when(store.read(eq(ConfigManager.ORDER))).thenReturn(Collections.emptyList());
        assertEquals(simpleOptions("opt1", "opt2"), configManager.getOptions());
        verify(store, times(1)).read(anyString());
    }

    @Test
    public void saveOption_add_options_in_store() throws Exception {
        when(store.read(eq(ConfigManager.ORDER))).thenReturn(Collections.singletonList("old"));
        configManager.saveOption("new", Arrays.asList("1", "2"));

        InOrder inOrder = Mockito.inOrder(store);
        inOrder.verify(store).save(eq(ConfigManager.ORDER), eq(Arrays.asList("old", "new")));
        inOrder.verify(store).save(eq(ConfigManager.KEY_PREFIX + "new"), eq(Arrays.asList("1", "2")));
    }

    @Test
    public void saveOption_updates_option_in_store() throws Exception {
        when(store.read(eq(ConfigManager.ORDER))).thenReturn(Collections.singletonList("old"));
        configManager.saveOption("old", Arrays.asList("1", "2"));

        verify(store).save(eq(ConfigManager.KEY_PREFIX + "old"), eq(Arrays.asList("1", "2")));
        verify(store).save(anyString(), any());
    }

    @Test(expected = Exception.class)
    public void saveOption_throws_on_bad_size() throws Exception {
        when(store.read(eq(ConfigManager.ORDER))).thenReturn(Collections.emptyList());
        configManager.saveOption("test", Collections.singletonList("1"));
    }

    @Test
    public void getTargetIds() throws Exception {
        assertEquals(config.getTargetIds(), configManager.getTargetIds());
    }

    private static List<FillOption> simpleOptions(String... ids) {
        return Stream.of(ids).map(ConfigManagerTest::simpleOption).collect(Collectors.toList());
    }

    private static FillOption simpleOption(String id) {
        return new FillOption("n" + id, Arrays.asList("v" + id + "1", "v" + id + "2"));
    }
}