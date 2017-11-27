package com.mindcoders.phial.internal.overlay;

import android.app.Activity;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.internal.PhialNotifier;
import com.mindcoders.phial.internal.Screen;
import com.mindcoders.phial.internal.ScreenTracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by rost on 11/27/17.
 */
@RunWith(RobolectricTestRunner.class)
public class OverlayPresenterTest {
    private OverlayPresenter presenter;
    private List<Page> pages;

    @Mock
    private OverlayView view;
    @Mock
    private SelectedPageStorage selectedPageStorage;
    @Mock
    private ScreenTracker screenTracker;
    @Mock
    private PhialNotifier notifier;
    @Mock
    private Activity activity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        pages = new ArrayList<>();
        presenter = new OverlayPresenter(view, pages, selectedPageStorage, screenTracker, notifier);
    }

    @Test
    public void onActivityStarted_adds_button() {
        presenter.onActivityStarted(activity);
        verify(view).showButton(eq(activity), eq(false));
    }

    @Test
    public void onActivityStopped_removes_button() {
        presenter.onActivityStopped(activity);
        verify(view).removeButton(eq(activity));
    }

    @Test
    public void onActivityResumed_does_noting_if_not_expanded() {
        verifyNoMoreInteractions(view);
        presenter.onActivityResumed(activity);
    }

    @Test
    public void onActivityResumed_saves_activity() {
        presenter.onActivityResumed(activity);
        assertEquals(activity, presenter.getCurActivity());
    }

    @Test
    public void onActivityPaused_does_noting_if_not_expanded() {
        verifyNoMoreInteractions(view);
        presenter.onActivityPaused(activity);
    }

    @Test
    public void onActivityResumed_shows_button_if_expanded_and_no_pages() {
        presenter.setExpanded(true);
        presenter.onActivityResumed(activity);
        verify(view, never()).showExpandedView(any(), any(), any(), anyBoolean());
        verify(view).freeExpandedContent();
        verify(view).showButton(eq(activity), eq(false));
    }

    @Test
    public void onActivityResumed_shows_expanded_if_expanded_and_has_pages() {
        addPages(2);
        mockScreenTrackerToMatchAll();
        mockStoreToSelect(pages.get(0));

        presenter.setExpanded(true);
        presenter.onActivityResumed(activity);

        verify(view, never()).freeExpandedContent();
        verify(view, never()).showButton(eq(activity), anyBoolean());
        verify(view).showExpandedView(eq(activity), eq(pages), eq(pages.get(0)), eq(false));
    }

    @Test
    public void onActivityPaused_ignores_pause_of_other_activity() {
        verifyNoMoreInteractions(view);

        presenter.setExpanded(true);
        presenter.onActivityResumed(activity);
        presenter.onActivityPaused(Mockito.mock(Activity.class));
    }

    @Test
    public void onActivityPaused_removes_expanded_view_if_expanded() {
        presenter.onActivityResumed(activity);
        presenter.setExpanded(true);
        presenter.onActivityPaused(activity);

        verify(view, never()).freeExpandedContent();
        verify(view).removeExpandedView(eq(activity));
    }

    @Test
    public void onButtonMoved_does_nothing_if_empty_pages() {
        presenter.setCurActivity(activity);

        verifyNoMoreInteractions(view, notifier);
        presenter.onButtonMoved();
    }

    @Test
    public void onButtonMoved_only_fires_notifier_if_not_resumed() throws Exception {
        addPages(2);
        mockScreenTrackerToMatchAll();
        mockStoreToSelect(pages.get(1));

        presenter.setExpanded(false);
        presenter.setCurActivity(null);
        verifyNoMoreInteractions(view);
        presenter.onButtonMoved();

        verify(notifier).fireDebugWindowShown();
        assertEquals(true, presenter.isExpanded());
    }

    @Test
    public void onButtonMoved_fires_notifier_shows_expanded_if_resumed_and_has_pages() {
        addPages(2);
        mockScreenTrackerToMatchAll();
        mockStoreToSelect(pages.get(1));

        presenter.setExpanded(false);
        presenter.setCurActivity(activity);
        presenter.onButtonMoved();

        verify(notifier).fireDebugWindowShown();
        verify(view).removeButton(eq(activity));
        verify(view).showExpandedView(eq(activity), eq(pages), eq(pages.get(1)), eq(true));
    }

    @Test
    public void onExpandedViewHidden_not_interact_view_if_paused() {
        presenter.setCurActivity(null);
        presenter.setExpanded(true);
        verifyNoMoreInteractions(view);

        presenter.onExpandedViewHidden();
        verify(notifier).fireDebugWindowHide();
    }

    @Test
    public void onExpandedViewHidden_hides_view_if_resumed() {
        presenter.setCurActivity(activity);
        presenter.setExpanded(true);

        presenter.onExpandedViewHidden();
        verify(notifier).fireDebugWindowHide();
        verify(view).freeExpandedContent();
        verify(view).removeExpandedView(eq(activity));
        verify(view).showButton(eq(activity), eq(true));
    }

    @Test
    public void onPageSelected_ignores_if_not_expanded() {
        addPages(2);
        presenter.setExpanded(false);
        verifyNoMoreInteractions(view, selectedPageStorage);
        presenter.onPageSelected(pages.get(0));
    }

    @Test
    public void onPageSelected_stores_new_selection() {
        addPages(2);

        presenter.setExpanded(true);
        presenter.onPageSelected(pages.get(1));

        verify(selectedPageStorage).setSelectedPage(eq(pages.get(1).getId()));
        verify(view).setSelectedPage(eq(pages.get(1)));
    }

    public void onScreenChange_ignores_if_not_expanded_or_paused() {
        verifyNoMoreInteractions(view, notifier);

        presenter.setExpanded(false);
        presenter.setCurActivity(activity);
        presenter.onScreenChanged(mock(Screen.class));

        presenter.setExpanded(true);
        presenter.setCurActivity(null);
        presenter.onScreenChanged(mock(Screen.class));

        presenter.setExpanded(false);
        presenter.setCurActivity(null);
        presenter.onScreenChanged(mock(Screen.class));
    }

    @Test
    public void onScreenChanged_updates_page() {
        addPages(2);
        mockScreenTrackerToMatchAll();
        mockStoreToSelect(pages.get(1));

        presenter.setExpanded(true);
        presenter.setCurActivity(activity);
        presenter.onScreenChanged(mock(Screen.class));

        verify(notifier, never()).fireDebugWindowShown();
        verify(view, never()).removeButton(any());
        verify(view).updateExpandedView(eq(pages), eq(pages.get(1)));
    }

    @Test
    public void onScreenChanged_closes_if_no_pages() {
        presenter.setExpanded(true);
        presenter.setCurActivity(activity);
        presenter.onScreenChanged(mock(Screen.class));

        verify(notifier).fireDebugWindowHide();
        verify(view).showButton(eq(activity), anyBoolean());
        verify(view).freeExpandedContent();
        verify(view).removeExpandedView(eq(activity));
    }

    @Test
    public void calcVisiblePages() throws Exception {
        addPages(2);
        when(screenTracker.matchesAny(any())).thenReturn(true, false);
        assertEquals(Collections.singletonList(pages.get(0)), presenter.calcVisiblePages());
    }

    @Test
    public void findSelected() {
        addPages(2);
        mockStoreToSelect(pages.get(1));
        assertEquals(pages.get(1), presenter.findSelected(pages));
    }

    private void addPages(int count) {
        for (int i = 0; i < count; i++) {
            pages.add(new Page(String.valueOf(i), i, "title" + i, null));
        }
    }

    private void mockScreenTrackerToMatchAll() {
        when(screenTracker.matchesAny(any())).thenReturn(true);
    }

    private void mockStoreToSelect(Page page) {
        when(selectedPageStorage.getSelectedPage()).thenReturn(page.getId());
    }
}