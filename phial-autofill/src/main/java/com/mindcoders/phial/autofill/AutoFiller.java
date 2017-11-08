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
    private static final String EMPTY_FIELD = null;
    private static final ScreenTracker SCREEN_TRACKER = new ScreenTracker();

    public static Page createPhialPage(Application application, List<FillConfig> autoFillers) {

        application.registerActivityLifecycleCallbacks(SCREEN_TRACKER);
        AutoFillPageFactory pageFactory = new AutoFillPageFactory(autoFillers, SCREEN_TRACKER);

        return new Page("autofill", R.drawable.ic_paste, "AutoFill", pageFactory);
    }

    public static Page createPhialPage(Application application, FillConfig... autoFillers) {
        return createPhialPage(application, Arrays.asList(autoFillers));
    }

    public static AutoFillerBuilder forActivity(Class<? extends Activity> target) {
        return new AutoFillerBuilder(TargetScreen.forActivity(target));
    }

    public static AutoFillerBuilder forScope(String scope) {
        return new AutoFillerBuilder(TargetScreen.forScope(scope));
    }

    public static void enterScope(String scope) {
        SCREEN_TRACKER.enterScope(scope);
    }

    public static void exitScope(String scope) {
        SCREEN_TRACKER.exitScope(scope);
    }

    public static FillOption option(String name, String... dataToFill) {
        Precondition.notEmpty(dataToFill, "dataToFill should not be empty");
        return new FillOption(name, Arrays.asList(dataToFill));
    }

    public static String leaveEmpty() {
        return EMPTY_FIELD;
    }
}
