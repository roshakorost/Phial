package com.mindcoders.phial.internal.share;

import android.content.ComponentName;
import android.support.annotation.VisibleForTesting;

import com.mindcoders.phial.ShareDescription;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

/**
 * Created by rost on 11/28/17.
 */
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
public class SystemShareItem extends ShareItem {
    private final ComponentName componentName;

    SystemShareItem(ShareDescription shareDescription, ComponentName componentName) {
        super(shareDescription);
        this.componentName = componentName;
    }

    ComponentName getComponentName() {
        return componentName;
    }
}
