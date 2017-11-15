package com.mindcoders.phial.internal.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Created by rost on 11/13/17.
 */
public class CollectionUtilsTest {

    @Test
    public void filter() {
        final List<String> strings = Arrays.asList("a", "aa", "aa", "aaa", "aaa", "aaaa");
        Assert.assertEquals(
                Arrays.asList("aaa", "aaa", "aaaa"),
                CollectionUtils.filter(strings, str -> str.length() > 2)
        );
    }

    @Test
    public void map_list() {
        final List<String> strings = Arrays.asList("a", "aa", "aa", "aaa", "aaa", "aaaa");
        Assert.assertEquals(
                Arrays.asList("ba", "baa", "baa", "baaa", "baaa", "baaaa"),
                CollectionUtils.map(strings, str -> "b" + str)
        );
    }

    @Test
    public void map_set() {
        final List<String> strings = Arrays.asList("a", "aa", "aaa", "aaaa");
        Assert.assertEquals(
                new HashSet<>(Arrays.asList("ba", "baa", "baaa", "baaaa")),
                CollectionUtils.map(new HashSet<>(strings), str -> "b" + str)
        );
    }

    @Test
    public void isNullOrEmpty() {
        final List<String> strings = Arrays.asList("a", "aa", "aaa", "aaaa");
        Assert.assertFalse(CollectionUtils.isNullOrEmpty(strings));
        Assert.assertTrue(CollectionUtils.isNullOrEmpty(null));
        Assert.assertTrue(CollectionUtils.isNullOrEmpty(Collections.emptyList()));
    }

    //we have features of java 8 only in tests
    @Test
    public void asList() throws Exception {
        final int[] ints = IntStream.range(0, 5).toArray();
        final List<Integer> expected = IntStream.range(0, 5).boxed().collect(Collectors.toList());
        Assert.assertEquals(expected, CollectionUtils.asList(ints));
    }

    @Test
    public void asSet() throws Exception {
        final Set<Integer> expected = IntStream.range(0, 5).boxed().collect(Collectors.toSet());
        Assert.assertEquals(expected, CollectionUtils.asSet(0, 1, 2, 3, 4));
    }

}