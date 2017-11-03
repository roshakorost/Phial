package com.mindcoders.phial.autofill;


import android.support.annotation.NonNull;

import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.Arrays;
import java.util.List;

public class AutoFillerBuilder {
    private final Screen screenToFill;
    private List<Integer> targetIds;
    private List<FillOption> options;

    AutoFillerBuilder(@NonNull Screen screenToFill) {
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

        return new FillConfig(screenToFill, targetIds, options);
    }
}
