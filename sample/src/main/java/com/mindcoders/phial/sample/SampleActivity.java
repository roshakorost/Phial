package com.mindcoders.phial.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mindcoders.phial.keyvalue.Phial;

import timber.log.Timber;


public class SampleActivity extends AppCompatActivity {

    private final ItemRepository itemRepository = new ItemRepository();
    private TextView itemNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");

        setContentView(R.layout.activity_main);
        itemNameTV = findViewById(R.id.text);
        showCurrentItem();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Phial.setKey("currentActivity", getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Phial.removeKey("currentActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");
    }

    private void showCurrentItem() {
        final ItemRepository.Item item = itemRepository.loadItem();

        //log will get information about every click
        // and about clicks from previous app start
        Timber.d("item loaded %s", item);
        //when KeyValue will have information only about last item.
        Phial.setKey("currentItem", item);

        itemNameTV.setText(item.getName());
    }
}
