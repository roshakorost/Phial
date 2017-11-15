package com.mindcoders.phial.autofill;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.PageView;
import com.mindcoders.phial.internal.util.Precondition;
import com.mindcoders.phial.internal.util.SimpleTextWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 11/3/17.
 */

class FillView extends FrameLayout implements PageView, Adapter.OnItemClickedListener {

    private final OverlayCallback overlayCallback;
    private final Adapter adapter;
    private final ConfigManager manager;

    FillView(@NonNull Context context) {
        super(context);
        Precondition.calledFromTools(this);
        overlayCallback = null;
        adapter = null;
        manager = null;
    }

    FillView(@NonNull Context context, ConfigManager manager, OverlayCallback overlayCallback) {
        super(context);
        this.manager = manager;
        this.overlayCallback = overlayCallback;
        this.adapter = new Adapter(context, this);

        final LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_autofill, this, true);

        final ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        final EditText nameTV = findViewById(R.id.name_tv);

        final View button = findViewById(R.id.add_button);
        button.setOnClickListener(v -> {
            saveOption(nameTV.getText().toString());
            presentOptions(manager.getOptions());
        });
        nameTV.addTextChangedListener(new ButtonEnabler(button));

        presentOptions(manager.getOptions());
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
        final Activity currentActivity = overlayCallback.getCurrentActivity();
        if (currentActivity == null) {
            return;
        }

        final List<String> dataToFill = option.getDataToFill();
        final List<Integer> ids = manager.getTargetIds();

        for (int i = 0; i < Math.min(dataToFill.size(), ids.size()); i++) {
            final View view = currentActivity.findViewById(ids.get(i));
            if (view instanceof TextView) {
                ((TextView) view).setText(dataToFill.get(i));
            }
        }

        overlayCallback.finish();
    }

    private void saveOption(String optionName) {
        final Activity currentActivity = overlayCallback.getCurrentActivity();
        if (currentActivity == null) {
            return;
        }

        if (optionName.isEmpty()) {
            return;
        }

        final List<Integer> ids = manager.getTargetIds();
        final List<String> values = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            final View view = currentActivity.findViewById(ids.get(i));
            final String value = readValueOrDefault(view);
            values.add(value);
        }
        manager.saveOption(optionName, values);
    }

    private String readValueOrDefault(View view) {
        if (!(view instanceof TextView)) {
            return AutoFiller.EMPTY_FIELD;
        }

        return ((TextView) view).getText().toString();
    }

    private static class ButtonEnabler extends SimpleTextWatcher {
        private final View view;

        private ButtonEnabler(View view) {
            this.view = view;
        }

        @Override
        public void afterTextChanged(Editable s) {
            view.setEnabled(!TextUtils.isEmpty(s));
        }
    }
}
