package com.mindcoders.phial.internal.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rost on 4/3/17.
 */

public class ComponentHolder {
    private final Map<Class<?>, Factory> factories = new HashMap<>();
    private final Map<Class<?>, Object> items = new HashMap<>();

    @SuppressWarnings("unchecked")
    public final <T> T provide(Class<T> forClazz) {
        synchronized (items) {
            Object result = items.get(forClazz);
            if (result == null) {
                result = factories.get(forClazz).create(this);
                items.put(forClazz, result);
            }
            return (T) result;
        }
    }

    public final <T> ComponentHolder registerFactory(Class<T> forClazz, Factory<T> factory) {
        factories.put(forClazz, factory);
        return this;
    }

    public final <T> ComponentHolder registerDependency(Class<T> forClazz, T dependency) {
        factories.put(forClazz, new Module<>(dependency));
        return this;
    }

    public interface Factory<T> {
        T create(ComponentHolder componentHolder);
    }

    private class Module<T> implements Factory<T> {
        private final T dependency;

        public Module(T dependency) {
            this.dependency = dependency;
        }

        @Override
        public T create(ComponentHolder componentHolder) {
            return dependency;
        }
    }
}
