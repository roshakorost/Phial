package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.view.View;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Creates Phial Page that will fill views automatically
 */
public class AutoFiller {

    static final String EMPTY_FIELD = "";

    /**
     * Creates Autofiller Page to be displayed in the overlay.
     * {@link #forActivity(Class)} and {@link #forScope(String)} in order to get
     * {@link AutoFillerBuilder} that will build config
     *
     * @param config specifies available autofill options and where to offer autofill.
     * @return pages to be added to {@link PhialBuilder}
     */
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

    /**
     * @see AutoFiller#createPhialPages(FillConfig...)
     */
    public static List<Page> createPhialPages(List<FillConfig> autoFillers) {
        return CollectionUtils.map(autoFillers, AutoFiller::createPhialPage);
    }

    /**
     * Creates Autofiller Pages to be displayed in the overlay.
     * {@link #forActivity(Class)} and {@link #forScope(String)} in order to get
     * {@link AutoFillerBuilder} that will build config
     *
     * @param autoFillers specifies available autofill options and where to offer autofill.
     * @return pages to be added to {@link PhialBuilder}
     */
    public static List<Page> createPhialPages(FillConfig... autoFillers) {
        return createPhialPages(Arrays.asList(autoFillers));
    }

    /**
     * Creates {@link AutoFillerBuilder} for target activity.
     *
     * @param target activity that when resumed will have {@link FillOption} available.
     * @return builder to set available {@link FillOption}.
     */
    public static AutoFillerBuilder forActivity(Class<? extends Activity> target) {
        return new AutoFillerBuilder(TargetScreen.forActivity(target));
    }

    /**
     * Creates {@link AutoFillerBuilder} for target scope.
     *
     * @param scope unique scope name.
     * @return builder to set available {@link FillOption}.
     */
    public static AutoFillerBuilder forScope(String scope) {
        return new AutoFillerBuilder(TargetScreen.forScope(scope));
    }

    /**
     * Creates {@link AutoFillerBuilder} for target scope.
     *
     * @param scope unique scope name.
     * @parav view scope view.
     * @return builder to set available {@link FillOption}.
     */
    public static AutoFillerBuilder forScope(String scope, View view) {
        return new AutoFillerBuilder(TargetScreen.forScope(scope));
    }

    /**
     * Factory method to construct {@link FillOption}.
     *
     * @param name       option name to be displayed in the autofill page.
     * @param dataToFill strings that will be inserted into targets.
     */
    public static FillOption option(String name, String... dataToFill) {
        Precondition.notEmpty(dataToFill, "dataToFill should not be empty");
        return new FillOption(name, Arrays.asList(dataToFill));
    }

    /**
     * @return value that when inserted will leave a field empty.
     */
    public static String leaveEmpty() {
        return EMPTY_FIELD;
    }

}
