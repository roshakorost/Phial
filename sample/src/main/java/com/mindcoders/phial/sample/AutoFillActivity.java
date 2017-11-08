package com.mindcoders.phial.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mindcoders.phial.keyvalue.Phial;

/**
 * Created by rost on 11/8/17.
 */

public class AutoFillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Phial.setKey("currentActivity", getClass().getSimpleName());

        setContentView(R.layout.activity_auto_fill);
        final Button loginButton = findViewById(R.id.login_button);
        loginButton.setText(R.string.login_and_back);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.<EditText>findViewById(R.id.password).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                    finish();
                    return true;
                }

                return false;
            }
        });
    }


}
