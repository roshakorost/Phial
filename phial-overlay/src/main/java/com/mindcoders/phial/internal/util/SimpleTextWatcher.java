package com.mindcoders.phial.internal.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Provides default(empty) implementation of TextWatcher
 */
public class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //optional
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //optional
    }

    @Override
    public void afterTextChanged(Editable s) {
        //optional
    }
}
