package com.mindcoders.phial.overlay;

import android.view.View;

interface PageViewFactory<T extends View> {

    T onPageCreate();

    void onPageDestroy(T view);

}
