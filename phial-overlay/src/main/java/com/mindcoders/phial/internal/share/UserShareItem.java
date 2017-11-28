package com.mindcoders.phial.internal.share;

import android.support.annotation.VisibleForTesting;

import com.mindcoders.phial.Shareable;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

/**
 * Created by rost on 11/28/17.
 */
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
public class UserShareItem extends ShareItem {
    private final Shareable shareable;

    UserShareItem(Shareable shareable) {
        super(shareable.getDescription());
        this.shareable = shareable;
    }

    public Shareable getShareable() {
        return shareable;
    }
}
