package com.mindcoders.phial.internal.overlay;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.FrameLayout;

class WrapperView extends FrameLayout {

    private OnBackPressedListener onBackPressedListener;

    WrapperView(@NonNull Context context) {
        super(context);
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            onBackPressedListener.onBackPressed();
        }

        return super.dispatchKeyEvent(event);
    }

    interface OnBackPressedListener {

        void onBackPressed();

    }

}
