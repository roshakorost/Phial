package com.mindcoders.phial.internal.keyvalue;


import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialCore;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;

public final class KeyValueView extends FrameLayout {

    private final ExpandableListView listView;

    private final KeyValueAdapter adapter;

    private final KVSaver categoryProvider = PhialCore.getInstance().getKvSaver();

    private final Observer observer = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            adapter.swapData(categoryProvider.getData());
        }
    };

    public KeyValueView(Context context) {
        super(context);
        inflate(context, R.layout.view_keyvalue, this);
        listView = findViewById(R.id.list_keyvalue);

        adapter = new KeyValueAdapter(LayoutInflater.from(context));
        listView.setAdapter((ExpandableListAdapter) adapter);

        adapter.swapData(categoryProvider.getData());
        categoryProvider.addObserver(observer);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        categoryProvider.deleteObserver(observer);
    }

}
