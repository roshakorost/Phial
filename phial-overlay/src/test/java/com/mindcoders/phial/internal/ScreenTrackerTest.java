package com.mindcoders.phial.internal;

import android.app.Activity;
import android.view.View;

import com.mindcoders.phial.internal.ScreenTracker.ScreenListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScreenTrackerTest {

    private ScreenTracker screenTracker;

    @Before
    public void setUp() throws Exception {
        screenTracker = new ScreenTracker();
    }

    @Test
    public void onActivityResumed() throws Exception {
        ScreenListener listener = mock(ScreenListener.class);
        screenTracker.addListener(listener);

        Activity activity = mock(Activity.class);
        screenTracker.onActivityResumed(activity);

        ArgumentCaptor<Screen> screenCaptor = ArgumentCaptor.forClass(Screen.class);
        verify(listener).onScreenChanged(screenCaptor.capture());

        Screen screen = screenCaptor.getValue();
        assertEquals(activity, screen.getActivity());
    }

    @Test
    public void onActivityPaused() throws Exception {
        ScreenListener listener = mock(ScreenListener.class);
        screenTracker.addListener(listener);

        Activity activity = mock(Activity.class);
        screenTracker.onActivityResumed(activity);
        screenTracker.onActivityPaused(activity);

        ArgumentCaptor<Screen> screenCaptor = ArgumentCaptor.forClass(Screen.class);

        verify(listener, times(2)).onScreenChanged(screenCaptor.capture());

        Screen screen = screenCaptor.getValue();

        assertNull(screen.getActivity());
    }

    @Test
    public void onEnterScope() throws Exception {
        ScreenListener listener = mock(ScreenListener.class);
        screenTracker.addListener(listener);

        screenTracker.onEnterScope("scope", mock(View.class));

        verify(listener).onScreenChanged(any());
    }

    @Test
    public void onExitScope() throws Exception {
        ScreenListener listener = mock(ScreenListener.class);
        screenTracker.addListener(listener);

        screenTracker.onExitScope("scope");

        verify(listener).onScreenChanged(any());
    }

}
