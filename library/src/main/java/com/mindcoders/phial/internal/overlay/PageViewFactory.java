package com.mindcoders.phial.internal.overlay;

import android.view.View;

interface PageViewFactory<T extends View> {

    T createPageView();

    void onPageDestroy(T view);

}
