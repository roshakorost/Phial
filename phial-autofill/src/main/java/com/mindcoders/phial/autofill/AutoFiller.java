package com.mindcoders.phial.autofill;

import android.app.Activity;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial_autofill.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rost on 11/3/17.
 */

public class AutoFiller {

    private static final String EMPTY_FIELD = null;

    public static Page createPhialPage(List<FillConfig> autoFillers) {
        AutoFillPageFactory pageFactory = new AutoFillPageFactory(autoFillers);

        Set<TargetScreen> screens = new HashSet<>();
        for (FillConfig autoFiller : autoFillers) {
            screens.add(autoFiller.getScreen());
        }

        return new Page("autofill", R.drawable.ic_paste, "AutoFill", pageFactory, screens);
    }

    public static Page createPhialPage(FillConfig... autoFillers) {
        return createPhialPage(Arrays.asList(autoFillers));
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
