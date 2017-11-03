package com.mindcoders.phial.autofill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

public class FillConfig {
    private final Screen screen;
    private final List<Integer> targetIds;
    private final List<FillOption> options;

    FillConfig(Screen screen, List<Integer> targetIds, List<FillOption> options) {
        this.screen = screen;
        this.targetIds = Collections.unmodifiableList(new ArrayList<>(targetIds));
        this.options = Collections.unmodifiableList(new ArrayList<>(options));
    }

    Screen getScreen() {
        return screen;
    }

    List<Integer> getTargetIds() {
        return targetIds;
    }

    List<FillOption> getOptions() {
        return options;
    }
}
