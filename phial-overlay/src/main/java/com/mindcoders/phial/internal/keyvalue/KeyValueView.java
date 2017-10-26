package com.mindcoders.phial.internal.keyvalue;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.Observable;
import java.util.Observer;

public final class KeyValueView extends FrameLayout implements Observer {
    private final KeyValueAdapter adapter;
    private final KVSaver kvSaver;

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
        ExpandableListView listView = findViewById(R.id.list_keyvalue);
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
    public void update(Observable o, Object arg) {
        updateData();
    }

    private void updateData() {
        adapter.swapData(kvSaver.getData());
    }
}
