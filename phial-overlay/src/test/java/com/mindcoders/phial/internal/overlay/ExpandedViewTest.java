package com.mindcoders.phial.internal.overlay;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableList;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.PageView;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.util.ObjectUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

@RunWith(RobolectricTestRunner.class)
public class ExpandedViewTest {

    private ExpandedView view;

    private ActivityController<Activity> activityController;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(Activity.class);
        activityController.create();
        view = new ExpandedView(activityController.get());
        activityController.get().setContentView(view);
    }

    @Test
    public void displayPages() throws Exception {
        View pageView1 = mock(View.class, withSettings().extraInterfaces(PageView.class));
        View pageView2 = mock(View.class, withSettings().extraInterfaces(PageView.class));

        List<Page> pages = ImmutableList.of(
                new Page("page1", R.drawable.ic_share, "page1", (context, overlayCallback) -> pageView1),
                new Page("page2", R.drawable.ic_share, "page2", (context, overlayCallback) -> pageView2)
        );

        Page selectedPage = pages.get(0);

        view.displayPages(pages, selectedPage, false);

        ViewGroup iconHolder = view.findViewById(R.id.tab_icons_holder);
        assertEquals(pages.size() + 1, iconHolder.getChildCount());
        for (int i = 0; i < iconHolder.getChildCount(); i++) {
            View child = iconHolder.getChildAt(i);
            assertTrue(child.isSelected() == ObjectUtil.equals(child.getTag(), selectedPage.getId()));
        }

        ViewGroup container = view.findViewById(R.id.content);
        assertEquals(1, container.getChildCount());

        assertEquals(View.VISIBLE, view.getVisibility());
    }

    @Test
    public void setSelected() throws Exception {
        View pageView1 = mock(View.class, withSettings().extraInterfaces(PageView.class));
        View pageView2 = mock(View.class, withSettings().extraInterfaces(PageView.class));

        List<Page> pages = ImmutableList.of(
                new Page("page1", R.drawable.ic_share, "page1", (context, overlayCallback) -> pageView1),
                new Page("page2", R.drawable.ic_share, "page2", (context, overlayCallback) -> pageView2)
        );

        view.displayPages(pages, pages.get(0), false);

        ViewGroup iconHolder = view.findViewById(R.id.tab_icons_holder);

        Page selectedPage = pages.get(1);
        view.setSelected(selectedPage);
        for (int i = 0; i < iconHolder.getChildCount(); i++) {
            View child = iconHolder.getChildAt(i);
            assertTrue(child.isSelected() == ObjectUtil.equals(child.getTag(), selectedPage.getId()));
        }
        View selectedView = iconHolder.findViewWithTag(selectedPage.getId());
        assertEquals(selectedPage.getId(), selectedView.getTag());
    }

    @Test
    public void destroyContent() throws Exception {
        view.destroyContent();
        assertEquals(View.INVISIBLE, view.getVisibility());
        ViewGroup container = view.findViewById(R.id.content);
        assertEquals(0, container.getChildCount());
    }

}
