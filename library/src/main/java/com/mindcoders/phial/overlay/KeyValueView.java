package com.mindcoders.phial.overlay;

import com.mindcoders.phial.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;

final class KeyValueView extends FrameLayout {

    private final ListView listView;

    private final KeyValueAdapter adapter;

    public KeyValueView(Context context) {
        super(context);
        inflate(context, R.layout.view_keyvalue, this);
        listView = findViewById(R.id.list_keyvalue);

        adapter = new KeyValueAdapter(LayoutInflater.from(context));
        listView.setAdapter(adapter);
    }

    public void swapData(List<Item> items) {
        adapter.swapData(items);
    }

}
