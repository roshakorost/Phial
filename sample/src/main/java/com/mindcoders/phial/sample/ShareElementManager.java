package com.mindcoders.phial.sample;

import android.support.annotation.StringRes;
import android.view.View;

/**
 * Created by rost on 11/8/17.
 */

public interface ShareElementManager {
    void addSharedElement(View item, @StringRes int stringNme);
}
