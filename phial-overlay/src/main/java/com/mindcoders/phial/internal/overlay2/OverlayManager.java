package com.mindcoders.phial.internal.overlay2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.util.SimpleActivityLifecycleCallbacks;

/**
 * Created by rost on 11/20/17.
 */

public class OverlayManager extends SimpleActivityLifecycleCallbacks {
    private final DragHelper dragHelper;
    private PhialButton button;

    public OverlayManager(Context context, SharedPreferences sharedPreferences) {
        final Context wrappedContext = new ContextThemeWrapper(context, R.style.Phial_Theme);
        this.button = new PhialButton(wrappedContext);
        this.button.setIcon(R.drawable.ic_handle);
        this.dragHelper = DragHelper.create(button, sharedPreferences);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        final FrameLayout root = getRootView(activity);
        root.addView(button, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        dragHelper.start();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        dragHelper.stop();
        final FrameLayout root = getRootView(activity);
        root.removeView(button);
    }

    private FrameLayout getRootView(Activity activity) {
        return (FrameLayout) activity.findViewById(android.R.id.content).getRootView();
    }
}
