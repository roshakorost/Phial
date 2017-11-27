package com.mindcoders.phial.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;

/**
 * Created by rost on 11/27/17.
 */
@RunWith(RobolectricTestRunner.class)
public class OverlayPresenterTest {
    private OverlayPresenter presenter;
    private ActivityController<Activity> activityController;
    private SharedPreferences sp;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(Activity.class);
        sp = RuntimeEnvironment.application
                .getSharedPreferences("OverlayPresenterTest", Context.MODE_PRIVATE);
        sp.edit().clear().commit();
    }

}