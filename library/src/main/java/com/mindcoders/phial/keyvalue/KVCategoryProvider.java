package com.mindcoders.phial.keyvalue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by rost on 10/22/17.
 */

public final class KVCategoryProvider extends Observable {
    private static final KVCategoryProvider instance = new KVCategoryProvider();
    private static final String DEFAULT_CATEGORY_NAME = null;

    public static KVCategoryProvider getInstance() {
        return instance;
    }

    private final ConcurrentMap<String, KVCategoryStore> categories = new ConcurrentHashMap<>();

    private KVCategoryProvider() {
        //to hide;
    }

    public KVSaver category(String name) {
        categories.putIfAbsent(name, new KVCategoryStore(name));
        return categories.get(name);
    }

    public KVSaver defaultCategory() {
        return category(DEFAULT_CATEGORY_NAME);
    }

    List<KVCategory> getCategories() {
        return Collections.unmodifiableList(new ArrayList<KVCategory>(categories.values()));
    }

    private class KVCategoryStore implements KVSaver, KVCategory {
        private final String categoryName;
        private final Map<String, String> keyValues = Collections.synchronizedMap(new LinkedHashMap<String, String>());

        KVCategoryStore(String categoryName) {
            this.categoryName = categoryName;
        }

        @Override
        public void setKey(String key, String value) {
            verifyKeyIsNotNull(key);
            keyValues.put(key, value);
            notifyObservers();
        }

        @Override
        public void setKey(String key, Object value) {
            setKey(key, String.valueOf(value));
            notifyObservers();
        }

        @Override
        public void removeKey(String key) {
            verifyKeyIsNotNull(key);
            keyValues.remove(key);
            notifyObservers();
        }

        private void verifyKeyIsNotNull(String key) {
            if (key == null) {
                throw new IllegalArgumentException("key should not be null.");
            }
        }

        @Override
        public String getName() {
            return categoryName;
        }

        @Override
        public List<KVEntry> entries() {
            final List<KVEntry> result = new ArrayList<>(keyValues.size());
            synchronized (keyValues) {
                for (Map.Entry<String, String> entry : keyValues.entrySet()) {
                    result.add(new KVEntry(entry.getKey(), entry.getValue()));
                }
            }
            return result;
        }
    }
}
