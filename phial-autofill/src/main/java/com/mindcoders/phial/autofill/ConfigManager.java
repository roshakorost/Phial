package com.mindcoders.phial.autofill;

import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 11/15/17.
 */

class ConfigManager {
    @VisibleForTesting
    static final String ORDER = "keys_order";
    @VisibleForTesting
    static final String KEY_PREFIX = "key_";
    private final FillConfig fillConfig;
    private final Store store;

    ConfigManager(FillConfig fillConfig, Store store) {
        this.fillConfig = fillConfig;
        this.store = store;
    }

    List<FillOption> getOptions() {
        final List<FillOption> savedOptions = readConfigsFromStore();
        final List<FillOption> result = new ArrayList<>(fillConfig.getOptions().size() + savedOptions.size());
        result.addAll(savedOptions);
        result.addAll(fillConfig.getOptions());
        return result;
    }

    void saveOption(String name, List<String> values) {
        if (!hasSameSizeAsConfig(values)) {
            throw new IllegalArgumentException("unexpected size of options!. Should be "
                    + fillConfig.getTargetIds().size()
                    + "; But was " + values.size()
            );
        }

        final List<String> optionNames = store.read(ORDER);
        if (!optionNames.contains(name)) {
            List<String> newOptions = new ArrayList<>(optionNames);
            newOptions.add(name);
            store.save(ORDER, newOptions);
        }

        store.save(KEY_PREFIX + name, values);
    }

    List<Integer> getTargetIds() {
        return fillConfig.getTargetIds();
    }

    private List<FillOption> readConfigsFromStore() {
        final List<String> optionNames = store.read(ORDER);
        final List<FillOption> options = new ArrayList<>(optionNames.size());
        for (String optionName : optionNames) {
            final List<String> values = store.read(KEY_PREFIX + optionName);
            //PageCreation might be updated and have less options or mo option
            //Since we not sure what to do in such case just ignore them as invalid;
            if (hasSameSizeAsConfig(values)) {
                final FillOption fillOption = new FillOption(optionName, values);
                options.add(fillOption);
            }
        }
        return options;
    }

    private boolean hasSameSizeAsConfig(List<String> values) {
        return values.size() == fillConfig.getTargetIds().size();
    }
}
