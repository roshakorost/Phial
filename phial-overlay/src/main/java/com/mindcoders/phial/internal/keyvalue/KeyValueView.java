package com.mindcoders.phial.internal.keyvalue;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import com.mindcoders.phial.PageView;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public final class KeyValueView extends FrameLayout implements Observer, PageView {
    private final KeyValueAdapter adapter;
    private final KVSaver kvSaver;
    private ExpandableListView listView;

    private boolean expandFirst = false;

    //only for Android Studio tests
    @VisibleForTesting
    public KeyValueView(@NonNull Context context) {
        super(context);
        Precondition.calledFromTools(this);
        adapter = null;
        kvSaver = null;
    }

    public KeyValueView(Context context, KVSaver kvSaver) {
        super(context);
        this.kvSaver = kvSaver;

        inflate(context, R.layout.view_keyvalue, this);
        listView = findViewById(R.id.list_keyvalue);
        adapter = new KeyValueAdapter(LayoutInflater.from(context));
        listView.setAdapter((ExpandableListAdapter) adapter);

        updateData();
        kvSaver.addObserver(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        kvSaver.deleteObserver(this);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {
        updateData();
    }

    private void updateData() {
        final List<KVSaver.KVCategory> data = kvSaver.getData();
        adapter.swapData(data);
        if (!expandFirst && !data.isEmpty()) {
            listView.expandGroup(0, true);
            expandFirst = true;
        }
    }
}
