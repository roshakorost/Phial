package com.mindcoders.phial.autofill;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.PageView;
import com.mindcoders.phial_autofill.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

class FillView extends FrameLayout implements PageView, ScreenTracker.ScreenListener, Adapter.OnItemClickedListener {
    private final ScreenTracker tracker;
    private final List<FillConfig> configs;
    private final OverlayCallback overlayCallback;
    private final Adapter adapter;

    FillView(@NonNull Context context, List<FillConfig> configs, ScreenTracker tracker, OverlayCallback overlayCallback) {
        super(context);
        this.tracker = tracker;
        this.configs = configs;
        this.overlayCallback = overlayCallback;
        this.adapter = new Adapter(context, this);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_autofill, this, true);

        final ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
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
        adapter.swapData(options);
    }

    @Override
    public void onItemClicked(FillOption option) {
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
