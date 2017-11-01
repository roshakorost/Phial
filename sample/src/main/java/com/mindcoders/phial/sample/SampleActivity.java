package com.mindcoders.phial.sample;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mindcoders.phial.keyvalue.Phial;

import timber.log.Timber;


public class SampleActivity extends AppCompatActivity {

    public static final String CLICKED_KEY = "clicked_key";
    private int clickCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.button);

        final SharedPreferences sp = getSharedPreferences("ClicksSharedPreferences", MODE_PRIVATE);
        clickCount = sp.getInt(CLICKED_KEY, 0);

        showClickCount(button, clickCount);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCount++;
                sp.edit().putInt(CLICKED_KEY, clickCount).apply();
                showClickCount(button, clickCount);
            }
        });
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

    private void showClickCount(Button view, int count) {
        // in log you will see log entry every time button is clicked
        Timber.d("showClickCount %d", count);
        // only last value will be associated with key. Every time these method is called value will be updated.
        Phial.category("Button").setKey("clicked", count);
        view.setText(getString(R.string.clicked, count));
    }
}
