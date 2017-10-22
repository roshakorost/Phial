package com.mindcoders.phial;

import com.mindcoders.phial.keyvalue.KVEntry;
import com.mindcoders.phial.keyvalue.KVSaver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by rost on 10/22/17.
 */

class KVStore implements KVSaver {
    private static final String DEFAULT_CATEGORY_NAME = null;
    private final CategoryKeyStore defaultCategoryKeyStore = new CategoryKeyStore(DEFAULT_CATEGORY_NAME);
    private final Map<Key, String> entries = Collections.synchronizedMap(new LinkedHashMap<Key, String>());

    @Override
    public void setKey(String key, String value) {
        defaultCategoryKeyStore.setKey(key, value);
    }

    @Override
    public void removeKey(String key) {
        defaultCategoryKeyStore.removeKey(key);
    }

    KVSaver category(String name) {
        return new CategoryKeyStore(name);
    }

    List<KVEntry> getEntries() {
        final List<KVEntry> result = new ArrayList<>(entries.size());
        synchronized (entries) {
            final Set<Map.Entry<Key, String>> entries = this.entries.entrySet();
            for (Map.Entry<Key, String> entry : entries) {
                result.add(createKVEntry(entry));
            }
        }
        return result;
    }

    private KVEntry createKVEntry(Map.Entry<Key, String> entry) {
        return new KVEntry(entry.getKey().category, entry.getKey().key, entry.getValue());
    }

    private class CategoryKeyStore implements KVSaver {
        private final String categoryName;

        CategoryKeyStore(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        public void setKey(String key, String value) {
            verifyKeyIsNotNull(key);
            entries.put(new Key(key, categoryName), value);
        }

        @Override
        public void removeKey(String key) {
            verifyKeyIsNotNull(key);
            entries.remove(new Key(key, categoryName));
        }

        private void verifyKeyIsNotNull(String key) {
            if (key == null) {
                throw new IllegalArgumentException("key should not be null.");
            }
        }
    }

    private static class Key {
        private final String key;
        private final String category;

        Key(String key, String category) {
            this.key = key;
            this.category = category;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key1 = (Key) o;

            if (!key.equals(key1.key)) return false;
            return category != null ? category.equals(key1.category) : key1.category == null;

        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + (category != null ? category.hashCode() : 0);
            return result;
        }
    }
}
