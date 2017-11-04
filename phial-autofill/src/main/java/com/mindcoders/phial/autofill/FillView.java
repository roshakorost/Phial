package com.mindcoders.phial.autofill;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.PageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

public class FillView extends FrameLayout implements PageView, ScreenTracker.ScreenListener {
    private final ScreenTracker tracker;
    private final List<FillConfig> configs;
    private final OverlayCallback overlayCallback;

    public FillView(@NonNull Context context, ScreenTracker tracker, List<FillConfig> configs, OverlayCallback overlayCallback) {
        super(context);
        this.tracker = tracker;
        this.configs = configs;
        this.overlayCallback = overlayCallback;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        tracker.addListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        tracker.removeListener(this);
    }

    @Override
    public void onScreenChanged(Screen screen) {
        final List<FillOption> options = new ArrayList<>();
        for (FillConfig config : configs) {
            if (screen.matches(config.getScreen())) {
                options.addAll(config.getOptions());
            }
        }
        presentOptions(options);
    }

    private void presentOptions(List<FillOption> options) {

    }

    void onOptionSelected(FillOption option) {
        final List<String> dataToFill = option.getDataToFill();
        final List<Integer> ids = option.getIds();
        final Screen currentScreen = tracker.getCurrentScreen();

        for (int i = 0; i < Math.min(dataToFill.size(), ids.size()); i++) {
            final View view = currentScreen.findTarget(ids.get(i));
            if (view instanceof TextView) {
                ((TextView) view).setText(dataToFill.get(i));
            }
        }

        overlayCallback.finish();
    }
}
