package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.app.Application;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial_autofill.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

public class AutoFiller {
    public static String EMPTY_FIELD = null;

    public static Page createPhialPage(Application application, List<FillConfig> autoFillers) {
        final ScreenTracker screenTracker = new ScreenTracker();
        application.registerActivityLifecycleCallbacks(screenTracker);

        AutoFillPageFactory pageFactory = new AutoFillPageFactory(autoFillers, screenTracker);

        return new Page("autofill", R.drawable.ic_paste, "AutoFill", pageFactory);
    }

    public static Page createPhialPage(Application application, FillConfig... autoFillers) {
        return createPhialPage(application, Arrays.asList(autoFillers));
    }

    public static AutoFillerBuilder forActivity(Class<? extends Activity> target) {
        return new AutoFillerBuilder(TargetScreen.from(target));
    }

    public static FillOption option(String name, String... dataToFill) {
        Precondition.notEmpty(dataToFill, "dataToFill should not be empty");
        return new FillOption(name, Arrays.asList(dataToFill));
    }

    public static String leaveEmpty() {
        return EMPTY_FIELD;
    }
}
