package com.mindcoders.phial.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mindcoders.phial.internal.keyvalue.KVSaver;
import com.mindcoders.phial.keyvalue.Phial;


public class SampleActivity extends AppCompatActivity {

    private final ItemRepository itemRepository = new ItemRepository();
    private TextView itemNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void showCurrentItem() {
        final ItemRepository.Item item = itemRepository.loadItem();
        Phial.setKey("currentItem", item);
        itemNameTV.setText(item.getName());
    }
}
