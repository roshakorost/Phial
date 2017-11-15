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

    /**
     * Constructs a new FillConfig.
     * @param screen screen where the specified options can be inserted.
     * @param options options that provide values to be inserted.
     */
    public FillConfig(TargetScreen screen, List<FillOption> options) {
        this.screen = screen;
        this.options = Collections.unmodifiableList(options);
    }

    TargetScreen getScreen() {
        return screen;
    }

    List<FillOption> getOptions() {
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FillConfig that = (FillConfig) o;

        if (!screen.equals(that.screen)) return false;
        return options.equals(that.options);
    }

    @Override
    public int hashCode() {
        int result = screen.hashCode();
        result = 31 * result + options.hashCode();
        return result;
    }

}
