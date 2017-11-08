package com.mindcoders.phial.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mindcoders.phial.keyvalue.Phial;

import timber.log.Timber;

/**
 * Created by rost on 11/8/17.
 */

public class MainFragment extends Fragment {
    public static final String CLICKED_KEY = "clicked_key";
    private int clickCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onCreate");

        final Button button = view.findViewById(R.id.button);
        ((ShareElementManager) getActivity()).addSharedElement(button, R.string.transition_button);

        final SharedPreferences sp = getContext().getSharedPreferences("ClicksSharedPreferences", Context.MODE_PRIVATE);
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
    public void onResume() {
        super.onResume();
        Timber.d("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("onPause");
    }

    @Override
    public void onDestroy() {
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
