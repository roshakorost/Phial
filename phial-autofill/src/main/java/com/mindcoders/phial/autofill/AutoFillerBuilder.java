package com.mindcoders.phial.autofill;


import android.text.TextUtils;

import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoFillerBuilder {
    private final TargetScreen targetScreen;
    private List<Integer> targetIds;
    private List<FillOption> options = new ArrayList<>();

    AutoFillerBuilder(TargetScreen targetScreen) {
        this.targetScreen = targetScreen;
    }

    /**
     * Sets view ids to fill.
     *
     * @param ids identifiers of views to be filled. Cannot be empty.
     */
    public AutoFillerBuilder fill(int... ids) {
        Precondition.notEmpty(ids, "ids should not be empty");
        this.targetIds = CollectionUtils.asList(ids);
        return this;
    }

    /**
     * Constructs {@link FillConfig} with provided fill ids and options.
     *
     * @param options options that provide values to be inserted. Can be empty.
     */
    public FillConfig withOptions(FillOption... options) {
        this.options = Arrays.asList(options);
        return this.build();
    }

    /**
     * Constructs {@link FillConfig} with provided fill ids but without options.
     * So user can manually add them
     */
    public FillConfig withoutOptions() {
        this.options = new ArrayList<>();
        return this.build();
    }

    private FillConfig build() {
        Precondition.notNull(targetIds, "fill target is not set");

        final List<FillOption> optionsWithTargets = new ArrayList<>(options.size());
        final int expectedSize = targetIds.size();

        for (FillOption option : options) {
            final List<String> dataToFill = option.getDataToFill();
            verifySizeMatches(expectedSize, option, dataToFill);
            optionsWithTargets.add(option);
        }
        return new FillConfig(targetScreen, optionsWithTargets, targetIds);
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
