package com.mindcoders.phial.autofill;

import com.mindcoders.phial.TargetScreen;

import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

public class FillConfig {
    private final TargetScreen screen;
    private final List<FillOption> options;
    private final List<Integer> targetIds;

    FillConfig(TargetScreen screen, List<FillOption> options, List<Integer> targetIds) {
        this.screen = screen;
        this.options = Collections.unmodifiableList(options);
        this.targetIds = Collections.unmodifiableList(targetIds);
    }

    TargetScreen getScreen() {
        return screen;
    }

    List<FillOption> getOptions() {
        return options;
    }

    List<Integer> getTargetIds() {
        return targetIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FillConfig that = (FillConfig) o;

        if (!screen.equals(that.screen)) return false;
        if (!options.equals(that.options)) return false;
        return targetIds.equals(that.targetIds);
    }

    @Override
    public int hashCode() {
        int result = screen.hashCode();
        result = 31 * result + options.hashCode();
        result = 31 * result + targetIds.hashCode();
        return result;
    }
}
