package com.mindcoders.phial;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mindcoders.phial.keyvalue.KVCategoryProvider;
import com.mindcoders.phial.keyvalue.KVSaver;
import com.mindcoders.phial.overlay.Overlay;
import com.mindcoders.phial.overlay.OverlayLifecycleCallbacks;
import com.mindcoders.phial.share.Shareable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public final class Phial {
    private static Phial instance;
    private final Context context;
    private final List<Shareable> userSharables = new ArrayList<>();

    public Phial(Context context) {
        this.context = context;
    }

    public static Phial getInstance() {
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public List<Shareable> getSharables() {
        return userSharables;
    }

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be set under default category.
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    public static void setKey(@NonNull String key, @Nullable String value) {
        KVCategoryProvider.getInstance().defaultCategory().setKey(key, value);
    }

    /**
     * Sets a value to be associated with your debug data.
     * Entry will be set under default category.
     * <p>
     * Note: setKey(key, null) will set associated value to null. In order to remove entry use {@link Phial#removeKey(String)}
     *
     * @param key   of the debug data
     * @param value associated value
     */
    public static void setKey(@NonNull String key, @Nullable Object value) {
        KVCategoryProvider.getInstance().defaultCategory().setKey(key, value);
    }

    /**
     * Removes entry with given key from debug data.
     * <p>
     * Note: Entry would be removed only from default category.
     *
     * @param key to be removed.
     */
    public static void removeKey(String key) {
        KVCategoryProvider.getInstance().defaultCategory().removeKey(key);
    }

    /**
     * Set's category name that will be associated with Key.
     */
    public static KVSaver category(String categoryName) {
        return KVCategoryProvider.getInstance().category(categoryName);
    }

    public static void init(Application application) {
        instance = new Phial(application);

        final Overlay overlay = new Overlay(application);
        OverlayLifecycleCallbacks overlayLifecycleCallbacks = new OverlayLifecycleCallbacks(overlay);
        application.registerActivityLifecycleCallbacks(overlayLifecycleCallbacks);
        SystemInfoWriter.writeSystemInfo(KVCategoryProvider.getInstance().defaultCategory(), application);
    }

}
