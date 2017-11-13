package com.mindcoders.phial.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CollectionUtils {

    private CollectionUtils() {
        // hide
    }


    public interface Predicate<T> {
        boolean apply(T type);
    }

    public interface Function1<T, G> {
        T call(G value);
    }

    public static <L> ArrayList<L> filter(List<L> list, Predicate<L> predicate) {
        ArrayList<L> result = new ArrayList<L>();
        for (L element : list) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <T, G> ArrayList<T> map(List<G> input, Function1<T, G> function) {
        ArrayList<T> result = new ArrayList<T>(input.size());
        for (G item : input) {
            result.add(function.call(item));
        }
        return result;
    }

    public static <T, G> Set<T> map(Set<G> input, Function1<T, G> function) {
        Set<T> result = new HashSet<T>(input.size());
        for (G item : input) {
            result.add(function.call(item));
        }
        return result;
    }

    public static boolean isNullOrEmpty(Collection<?> input) {
        return input == null || input.isEmpty();
    }

    public static List<Integer> asList(int[] ids) {
        final List<Integer> result = new ArrayList<>(ids.length);
        for (int id : ids) {
            result.add(id);
        }
        return result;
    }
}
