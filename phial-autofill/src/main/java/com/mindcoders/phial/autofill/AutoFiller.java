package com.mindcoders.phial.autofill;

import android.app.Activity;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

public class AutoFiller {
    public static Page createPhialPage(List<FillConfig> autoFillers) {
        return null;
    }

    public static Page createPhialPage(FillConfig... autoFillers) {
        return null;
    }

    public static AutoFillerBuilder forActivity(Class<? extends Activity> target) {
        return new AutoFillerBuilder(TargetScreen.from(target));
    }

    public static FillOption option(String name, String... dataToFill) {
        Precondition.notEmpty(dataToFill, "dataToFill should not be empty");
        return new FillOption(name, Arrays.asList(dataToFill));
    }
}
