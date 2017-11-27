package com.mindcoders.phial.internal;

import android.app.Activity;
import android.view.View;

import com.google.common.collect.ImmutableList;
import com.mindcoders.phial.TargetScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class ScreenTest {

    private Screen screen;

    @Before
    public void setUp() throws Exception {
        screen = Screen.empty();
    }

    @Test
    public void matches_activity() throws Exception {
        TargetScreen targetScreen = TargetScreen.forActivity(ActivityA.class);

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());

        assertTrue(screen.matches(targetScreen));
    }

    @Test
    public void matches_scope() throws Exception {
        TargetScreen targetScreen = TargetScreen.forScope("scope");

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());
        screen.enterScope("scope", null);

        assertTrue(screen.matches(targetScreen));
    }

    @Test
    public void matches_otherScopeActive() throws Exception {
        TargetScreen targetScreen = TargetScreen.forScope("scope1");

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());
        screen.enterScope("scope2", null);

        assertFalse(screen.matches(targetScreen));
    }

    @Test
    public void matches_afterExitScope() throws Exception {
        TargetScreen targetScreen = TargetScreen.forScope("scope");

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());
        screen.enterScope("scope", null);
        screen.exitScope("scope");

        assertFalse(screen.matches(targetScreen));
    }

    @Test
    public void matches_otherActivityResumed() {
        TargetScreen targetScreen = TargetScreen.forActivity(ActivityB.class);

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());

        assertFalse(screen.matches(targetScreen));
    }

    @Test
    public void matchesAny() throws Exception {
        Collection<TargetScreen> targetScreens = ImmutableList.of(
                TargetScreen.forScope("scope0"),
                TargetScreen.forScope("scope1")
        );

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());
        screen.enterScope("scope1", null);

        assertTrue(screen.matchesAny(targetScreens));
    }

    @Test
    public void matchesAny_empty() throws Exception {
        Collection<TargetScreen> targetScreens = Collections.emptyList();

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());

        assertTrue(screen.matchesAny(targetScreens));
    }

    @Test
    public void matchesAny_nothingMatches() throws Exception {
        Collection<TargetScreen> targetScreens = ImmutableList.of(
                TargetScreen.forScope("scope0"),
                TargetScreen.forScope("scope1")
        );

        screen.setActivity(Robolectric.buildActivity(ActivityA.class).get());
        screen.enterScope("scope2", null);

        assertFalse(screen.matchesAny(targetScreens));
    }

    @Test
    public void findTarget_activity() {
        Activity activity = mock(Activity.class);
        doReturn(mock(View.class)).when(activity).findViewById(1);
        screen.setActivity(activity);

        assertNotNull(screen.findTarget(1));
    }

    @Test
    public void findTarget_view() {
        View view = mock(View.class);
        doReturn(mock(View.class)).when(view).findViewById(1);

        screen.setActivity(mock(Activity.class));
        screen.enterScope("scope", view);

        assertNotNull(screen.findTarget(1));
    }

    @Test
    public void findTarget() {
        screen.setActivity(mock(Activity.class));

        assertNull(screen.findTarget(1));
    }

    private static class ActivityA extends Activity {}

    private static class ActivityB extends Activity {}

}
