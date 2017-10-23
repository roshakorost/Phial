package com.mindcoders.phial.internal;

import com.mindcoders.phial.internal.keyvalue.KVCategoryProvider;
import com.mindcoders.phial.internal.share.ShareManager;
import com.mindcoders.phial.internal.util.ComponentHolder;

/**
 * Created by rost on 10/23/17.
 */

public class PhialComponent extends ComponentHolder {
    private static PhialComponent instance = new PhialComponent();

    public static <T> T get(Class<T> forClazz) {
        return instance.provide(forClazz);
    }

    public static void init(PhialConfig config) {
        instance.registerDependency(PhialConfig.class, config);

        final ShareManager shareManager = new ShareManager(config.getApplication(), config.getUserSharables());
        instance.registerDependency(ShareManager.class, shareManager);
    }

    private PhialComponent() {
        registerDependency(KVCategoryProvider.class, new KVCategoryProvider());
    }
}

