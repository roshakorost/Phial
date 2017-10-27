package com.mindcoders.phial;

import android.content.Context;
import android.view.View;

/**
 * Created by rost on 10/27/17.
 */

public interface ShareContext {
    Context getAndroidContext();

    void onSuccess();

    void onFailed(String message);

    void presentView(View view);

    void setProgressBarVisibility(boolean isVisible);
}
