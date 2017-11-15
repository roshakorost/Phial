package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.PageView;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

class FillView extends FrameLayout implements PageView, Adapter.OnItemClickedListener {

    private final OverlayCallback overlayCallback;
    private final Adapter adapter;

    FillView(@NonNull Context context) {
        super(context);
        Precondition.calledFromTools(this);
        overlayCallback = null;
        adapter = null;
    }

    FillView(@NonNull Context context, FillConfig config, OverlayCallback overlayCallback) {
        super(context);

        this.overlayCallback = overlayCallback;
        this.adapter = new Adapter(context, this);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_autofill, this, true);

        final ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        presentOptions(config.getOptions());
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    private void presentOptions(List<FillOption> options) {
        adapter.swapData(options);
    }

    @Override
    public void onItemClicked(FillOption option) {
        final List<String> dataToFill = option.getDataToFill();
        final List<Integer> ids = option.getIds();

        for (int i = 0; i < Math.min(dataToFill.size(), ids.size()); i++) {
            final Activity currentActivity = overlayCallback.getCurrentActivity();
            if (currentActivity == null) {
                return;
            }

            final View view = currentActivity.findViewById(ids.get(i));
            if (view instanceof TextView) {
                ((TextView) view).setText(dataToFill.get(i));
            }
        }

        overlayCallback.finish();
    }
}
