package com.mindcoders.phial.internal.util;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by rost on 11/13/17.
 */
public class CurrentActivityProviderTest {

    private CurrentActivityProvider provider;

    @Before
    public void setUp() throws Exception {
        provider = new CurrentActivityProvider();
    }

    @Test
    public void getActivity_returns_null_at_start() throws Exception {
        assertNull(provider.getActivity());
    }

    @Test
    public void getActivity_returns_last_activity_if_it_is_not_paused() throws Exception {
        final Activity activity1 = Mockito.mock(Activity.class);
        provider.onActivityResumed(activity1);
        assertEquals(activity1, provider.getActivity());

        final Activity activity2 = Mockito.mock(Activity.class);
        provider.onActivityResumed(activity2);
        assertEquals(activity2, provider.getActivity());
    }

    @Test
    public void getActivity_returns_null_if_last_is_paused() throws Exception {
        final Activity activity1 = Mockito.mock(Activity.class);
        provider.onActivityResumed(activity1);
        provider.onActivityPaused(activity1);

        assertNull(provider.getActivity());
    }

    @Test
    public void getActivity_returns_last_if_old_is_paused() throws Exception {
        final Activity activity1 = Mockito.mock(Activity.class);
        provider.onActivityResumed(activity1);

        final Activity activity2 = Mockito.mock(Activity.class);
        provider.onActivityResumed(activity2);
        provider.onActivityPaused(activity1);

        assertEquals(activity2, provider.getActivity());
    }

    @Test
    public void background_foreground_is_tracked_correctly() {
        final CurrentActivityProvider.AppStateListener listener
                = Mockito.mock(CurrentActivityProvider.AppStateListener.class);

        final Activity activity1 = Mockito.mock(Activity.class);
        final Activity activity2 = Mockito.mock(Activity.class);

        provider.addListener(listener);
        Mockito.verifyZeroInteractions(listener);

        provider.onActivityResumed(activity1);
        Mockito.verify(listener, Mockito.times(1)).onAppForeground();
        provider.onActivityPaused(activity1);

        Mockito.verify(listener, Mockito.times(1)).onAppForeground();
        Mockito.verify(listener, Mockito.times(1)).onAppBackground();
        Mockito.reset(listener);

        provider.onActivityResumed(activity1);
        Mockito.verify(listener, Mockito.times(1)).onAppForeground();
        provider.onActivityResumed(activity2);
        provider.onActivityPaused(activity1);
        provider.onActivityPaused(activity2);

        Mockito.verify(listener, Mockito.times(1)).onAppForeground();
        Mockito.verify(listener, Mockito.times(1)).onAppBackground();
    }
}