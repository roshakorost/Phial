package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial.keyvalue.Phial;
import com.mindcoders.phial_autofill.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rost on 11/3/17.
 */

public class AutoFiller {
    public static final String EMPTY_FIELD = null;

    public static Page createPhialPage(Application application, List<FillConfig> autoFillers) {
        final Set<String> targetKeys = findTargetKeys(autoFillers);
        final ScreenTracker screenTracker = new ScreenTracker(targetKeys);

        application.registerActivityLifecycleCallbacks(screenTracker);
        if (!targetKeys.isEmpty()) {
            Phial.addSaver(screenTracker);
        }

        AutoFillPageFactory pageFactory = new AutoFillPageFactory(autoFillers, screenTracker);

        return new Page("autofill", R.drawable.ic_paste, "AutoFill", pageFactory);
    }

    @NonNull
    private static Set<String> findTargetKeys(List<FillConfig> autoFillers) {
        final Set<String> targetKeys = new HashSet<>();
        for (FillConfig autoFiller : autoFillers) {
            targetKeys.add(autoFiller.getScreen().getTargetKey());
        }
        return targetKeys;
    }

    public static Page createPhialPage(Application application, FillConfig... autoFillers) {
        return createPhialPage(application, Arrays.asList(autoFillers));
    }

    public static AutoFillerBuilder forActivity(Class<? extends Activity> target) {
        return new AutoFillerBuilder(target);
    }

    public static AutoFillerBuilder forKeyValue(String group, String key, String value) {
        return new AutoFillerBuilder(group, key, value);
    }

    public static AutoFillerBuilder forKeyValue(String key, String value) {
        return new AutoFillerBuilder(key, value);
    }

    public static FillOption option(String name, String... dataToFill) {
        Precondition.notEmpty(dataToFill, "dataToFill should not be empty");
        return new FillOption(name, Arrays.asList(dataToFill));
    }

    public static String leaveEmpty() {
        return EMPTY_FIELD;
    }
}
