package com.mindcoders.phial.autofill;


import android.support.annotation.NonNull;

import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoFillerBuilder {
    private final TargetScreen screenToFill;
    private List<Integer> targetIds;
    private List<FillOption> options;

    AutoFillerBuilder(@NonNull TargetScreen screenToFill) {
        this.screenToFill = screenToFill;
    }

    public AutoFillerBuilder fill(int... ids) {
        Precondition.notEmpty(ids, "ids should not be empty");
        this.targetIds = CollectionUtils.asList(ids);
        return this;
    }

    public AutoFillerBuilder withOptions(FillOption... options) {
        Precondition.notEmpty(options, "options should not be empty. See AutoFiller.option");
        this.options = Arrays.asList(options);
        return this;
    }

    public FillConfig build() {
        Precondition.notNull(targetIds, "fill target is not set");
        Precondition.notNull(options, "options are not set");

        final List<FillOption> optionsWithTargets = new ArrayList<>(options.size());
        for (FillOption option : options) {
            optionsWithTargets.add(option.withIds(targetIds));
        }
        return new FillConfig(screenToFill, optionsWithTargets);
    }
}
