package com.mindcoders.phial.internal.keyvalue;


import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialComponent;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ListView;

public final class KeyValueView extends FrameLayout {

    private final ListView listView;

    private final KeyValueAdapter adapter;

    private final CategoriesConverter converter = new CategoriesConverter();

    private final KVCategoryProvider categoryProvider = PhialComponent.get(KVCategoryProvider.class);

    private final Observer observer = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            adapter.swapData(converter.transform(categoryProvider.getCategories()));
        }
    };

    public KeyValueView(Context context) {
        super(context);
        inflate(context, R.layout.view_keyvalue, this);
        listView = findViewById(R.id.list_keyvalue);

        adapter = new KeyValueAdapter(LayoutInflater.from(context));
        listView.setAdapter(adapter);

        adapter.swapData(converter.transform(categoryProvider.getCategories()));
        categoryProvider.addObserver(observer);
    }

    public void onDestroy() {
        categoryProvider.deleteObserver(observer);
    }
}
