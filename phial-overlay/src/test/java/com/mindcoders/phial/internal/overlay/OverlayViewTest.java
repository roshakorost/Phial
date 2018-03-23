package com.mindcoders.phial.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.mindcoders.phial.Page;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricTestRunner.class)
public class OverlayViewTest {
    private OverlayView view;
    @Mock
    private DragHelper dragHelper;
    @Mock
    private ExpandedView expandedView;
    @Mock
    private WindowManager windowManager;
    private Activity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = mockActivity(windowManager);
        final Context baseContext = RuntimeEnvironment.application;
        view = new OverlayView(baseContext, dragHelper, expandedView);
    }

    @Test
    public void show_and_remove_button_single_activity() {
        view.showButton(activity, false);

        final ArgumentCaptor<View> viewArgumentCaptor = ArgumentCaptor.forClass(View.class);
        verify(windowManager).addView(viewArgumentCaptor.capture(), any());
        view.removeButton(activity);
        verify(windowManager).removeView(viewArgumentCaptor.getValue());
    }

    @Test
    public void show_and_remove_button_single_activity_animated() {
        view.showButton(activity, true);
        final ArgumentCaptor<View> viewArgumentCaptor = ArgumentCaptor.forClass(View.class);
        verify(windowManager).addView(viewArgumentCaptor.capture(), any());
        verify(dragHelper).animateFromDefaultPosition(eq(viewArgumentCaptor.getValue()), any());
        view.removeButton(activity);
        verify(windowManager).removeView(viewArgumentCaptor.getValue());
    }

    @Test
    public void remove_twice_the_same_no_crash() {
        view.showButton(activity, true);

        view.removeButton(activity);
        view.removeButton(activity);

        verify(dragHelper, Mockito.never()).unmanage(Matchers.isNull(View.class));
    }

    @Test
    public void show_and_remove_button_several_activities() {
        final ArgumentCaptor<View> viewArgumentCaptor = ArgumentCaptor.forClass(View.class);

        final WindowManager windowManager1 = mock(WindowManager.class);
        final Activity activity1 = mockActivity(windowManager1);
        final ArgumentCaptor<View> viewArgumentCaptor1 = ArgumentCaptor.forClass(View.class);

        view.showButton(activity, false);
        verify(windowManager).addView(viewArgumentCaptor.capture(), any());

        view.showButton(activity1, false);
        verify(windowManager1).addView(viewArgumentCaptor1.capture(), any());

        view.removeButton(activity);
        verify(windowManager).removeView(viewArgumentCaptor.getValue());

        view.removeButton(activity1);
        verify(windowManager1).removeView(viewArgumentCaptor1.getValue());
    }

    @Test
    public void showExpandedView() {
        final List<Page> pages = IntStream.range(0, 5).mapToObj(this::createPage).collect(Collectors.toList());
        view.showExpandedView(activity, pages, pages.get(1), false);
        verify(windowManager).addView(eq(expandedView), any());
        verify(expandedView).displayPages(eq(pages), eq(pages.get(1)), eq(false));
    }

    @Test
    public void updateExpandedView() {
        final List<Page> pages = IntStream.range(0, 5).mapToObj(this::createPage).collect(Collectors.toList());
        view.updateExpandedView(pages, pages.get(1));
        verify(expandedView).displayPages(eq(pages), eq(pages.get(1)), eq(false));
    }

    @Test
    public void removeExpandedView() {
        view.removeExpandedView(activity);
        verify(windowManager).removeView(eq(expandedView));
    }

    @Test
    public void setSelectedPage() {
        final Page page = createPage(1);
        view.setSelectedPage(page);
        verify(expandedView).setSelected(eq(page));
    }

    @Test
    public void presenterInvocation() {
        final OverlayPresenter presenter = mock(OverlayPresenter.class);
        view.setPresenter(presenter);
        view.finish();
        verify(presenter).closeDebugWindow();

        view.findViewById(1);
        verify(presenter).findViewById(eq(1));

        final Page page = createPage(1);
        view.onPageSelected(page);
        verify(presenter).onPageSelected(page);
    }

    private Activity mockActivity(WindowManager windowManager) {
        Activity activity = mock(Activity.class);
        when(activity.getWindowManager()).thenReturn(windowManager);
        return activity;
    }

    private Page createPage(int i) {
        return new Page(String.valueOf(i), i, "title" + i, null);
    }
}