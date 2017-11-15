package com.mindcoders.phial;

import android.app.Activity;

import com.mindcoders.phial.internal.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rost on 11/15/17.
 */

public class PhialPageBuilder {
    private final String id;
    private final int iconResourceId;
    private final CharSequence title;
    private final Page.PageViewFactory pageViewFactory;
    private final Set<TargetScreen> screens = new HashSet<>();

    public PhialPageBuilder(String id, int iconResourceId, CharSequence title, Page.PageViewFactory pageViewFactory) {
        this.id = id;
        this.iconResourceId = iconResourceId;
        this.title = title;
        this.pageViewFactory = pageViewFactory;
    }

    /**
     * Sets activities on which the page should be visible.
     * @return the same instance of {@link PhialPageBuilder}
     */
    public PhialPageBuilder visibleOnActivities(Class<? extends Activity>... activities) {
        return this.visibleOnActivities(Arrays.asList(activities));
    }

    /**
     * @see PhialPageBuilder#visibleOnActivities
     */
    public PhialPageBuilder visibleOnActivities(List<Class<? extends Activity>> activities) {
        final ArrayList<TargetScreen> screensToAdd = CollectionUtils.map(activities,
                new CollectionUtils.Function1<TargetScreen, Class<? extends Activity>>() {
                    @Override
                    public TargetScreen call(Class<? extends Activity> clazz) {
                        return TargetScreen.forActivity(clazz);
                    }
                });
        screens.addAll(screensToAdd);
        return this;
    }

    /**
     * Sets scopes in which the page should be visible.
     * @return the same instance of {@link PhialPageBuilder}
     */
    public PhialPageBuilder visibleOnScopes(String... scopes) {
        return this.visibleOnScopes(Arrays.asList(scopes));
    }

    /**
     * @see PhialPageBuilder#visibleOnScopes
     */
    public PhialPageBuilder visibleOnScopes(List<String> scopes) {
        final ArrayList<TargetScreen> screensToAdd = CollectionUtils.map(scopes,
                new CollectionUtils.Function1<TargetScreen, String>() {
                    @Override
                    public TargetScreen call(String scope) {
                        return TargetScreen.forScope(scope);
                    }
                });
        screens.addAll(screensToAdd);
        return this;
    }

    /**
     * Builds the page with provided settings.
     * @return a new instance of {@link Page}
     */
    public Page build() {
        return new Page(id, iconResourceId, title, pageViewFactory, screens);
    }
}
