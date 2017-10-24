package com.mindcoders.phial.internal.keyvalue;

import com.mindcoders.phialkv.Saver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Created by rost on 10/24/17.
 */

public class KVSaver extends Observable implements Saver {
    private final Map<String, Map<String, String>> items = new LinkedHashMap<>();

    @Override
    public void save(String category, String key, String value) {
        synchronized (items) {
            Map<String, String> keyValues = items.get(category);
            if (keyValues == null) {
                keyValues = new LinkedHashMap<>();
                items.put(category, keyValues);
            }
            keyValues.put(key, value);
        }
        notifyObservers();
    }

    @Override
    public void remove(String category, String key) {
        synchronized (items) {
            final Map<String, String> keyValues = items.get(category);
            if (keyValues != null) {
                keyValues.remove(key);
            }
        }
        notifyObservers();
    }

    List<KVCategory> getData() {
        final List<KVCategory> categories = new ArrayList<>();
        synchronized (items) {

            for (Map.Entry<String, Map<String, String>> categoryEntry : items.entrySet()) {
                final String categoryName = categoryEntry.getKey();
                final List<KVEntry> entries = new ArrayList<>();
                for (Map.Entry<String, String> keyValEntry : categoryEntry.getValue().entrySet()) {
                    final String key = keyValEntry.getKey();
                    final String value = keyValEntry.getValue();
                    entries.add(new KVEntry(key, value));
                }
                categories.add(new KVCategory(categoryName, entries));
            }

        }
        return categories;
    }

    static class KVEntry {
        private final String name;
        private final String value;

        KVEntry(String name, String value) {
            this.name = name;
            this.value = value;
        }

        String getName() {
            return name;
        }

        String getValue() {
            return value;
        }
    }

    static class KVCategory {
        private final String name;
        private final List<KVEntry> entries;

        KVCategory(String name, List<KVEntry> entries) {
            this.name = name;
            this.entries = entries;
        }

        String getName() {
            return name;
        }

        List<KVEntry> entries() {
            return entries;
        }

        boolean isEmpty() {
            return entries.isEmpty();
        }
    }
}
