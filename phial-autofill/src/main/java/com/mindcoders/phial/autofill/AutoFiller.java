package com.mindcoders.phial.autofill;

import android.app.Activity;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rost on 11/3/17.
 */

public class AutoFiller {

    private static final String EMPTY_FIELD = null;

    public static Page createPhialPage(FillConfig config) {
        final AutoFillPageFactory pageFactory = new AutoFillPageFactory(config);
        final TargetScreen screen = config.getScreen();

        return new Page("autofill" + screen.hashCode(),
                R.drawable.ic_paste,
                "Fill " + screen.getName(),
                pageFactory,
                Collections.singleton(screen)
        );
    }

    public static List<Page> createPhialPages(List<FillConfig> autoFillers) {
        return CollectionUtils.map(autoFillers, AutoFiller::createPhialPage);
    }

    public static List<Page> createPhialPages(FillConfig... autoFillers) {
        return createPhialPages(Arrays.asList(autoFillers));
    }

    public static AutoFillerBuilder forActivity(Class<? extends Activity> target) {
        return new AutoFillerBuilder(TargetScreen.forActivity(target));
    }

    public static AutoFillerBuilder forScope(String scope) {
        return new AutoFillerBuilder(TargetScreen.forScope(scope));
    }

    public static FillOption option(String name, String... dataToFill) {
        Precondition.notEmpty(dataToFill, "dataToFill should not be empty");
        return new FillOption(name, Arrays.asList(dataToFill));
    }

    public static String leaveEmpty() {
        return EMPTY_FIELD;
    }

}
