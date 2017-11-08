package com.mindcoders.phial.autofill;


import android.app.Activity;
import android.text.TextUtils;

import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial.keyvalue.Phial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoFillerBuilder {
    private Class<? extends Activity> activity;
    private String group, key, value;
    private List<Integer> targetIds;
    private List<FillOption> options;

    AutoFillerBuilder(Class<? extends Activity> activity) {
        setTargetActivity(activity);
    }

    AutoFillerBuilder(String group, String key, String value) {
        setKeyValue(group, key, value);
    }

    AutoFillerBuilder(String key, String value) {
        setKeyValue(key, value);
    }

    public AutoFillerBuilder setTargetActivity(Class<? extends Activity> activity) {
        Precondition.notNull(activity, "activity should not be empty");
        this.activity = activity;
        return this;
    }

    public AutoFillerBuilder setKeyValue(String group, String key, String value) {
        Precondition.notNull(key, "key should not be empty");
        this.group = group;
        this.key = key;
        this.value = value;
        return this;
    }

    public AutoFillerBuilder setKeyValue(String key, String value) {
        return setKeyValue(Phial.DEFAULT_CATEGORY_NAME, key, value);
    }

    public AutoFillerBuilder fill(int... ids) {
        Precondition.notEmpty(ids, "ids should not be empty");
        this.targetIds = CollectionUtils.asList(ids);
        return this;
    }

    public FillConfig withOptions(FillOption... options) {
        Precondition.notEmpty(options, "options should not be empty. See AutoFiller.option");
        this.options = Arrays.asList(options);
        return this.build();
    }

    private FillConfig build() {
        Precondition.notNull(targetIds, "fill target is not set");
        Precondition.notNull(options, "options are not set");

        final List<FillOption> optionsWithTargets = new ArrayList<>(options.size());
        final int expectedSize = targetIds.size();

        for (FillOption option : options) {
            final List<String> dataToFill = option.getDataToFill();
            verifySizeMatches(expectedSize, option, dataToFill);
            optionsWithTargets.add(option.withIds(targetIds));
        }
        return new FillConfig(new TargetScreen(activity, group, key, value), optionsWithTargets);
    }

    private static void verifySizeMatches(int expectedSize, FillOption option, List<String> dataToFill) {
        if (dataToFill.size() != expectedSize) {
            throw new IllegalArgumentException("Bad option " + option.getName() + " need to fill "
                    + expectedSize + "; But got only " + dataToFill.size() + " values. "
                    + TextUtils.join(", ", dataToFill)
                    + ". Use AutoFiller.leaveEmpty() if you don't need to fill some data"
            );
        }
    }
}
