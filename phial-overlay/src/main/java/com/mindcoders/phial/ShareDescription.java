package com.mindcoders.phial;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

/**
 * Created by rost on 10/22/17.
 */

public class ShareDescription {
    private final Drawable drawable;
    private final CharSequence label;

    public ShareDescription(Drawable drawable, CharSequence label) {
        this.drawable = drawable;
        this.label = label;
    }

    public ShareDescription(Context context, @DrawableRes int drawableRes, @StringRes int label) {
        this(ContextCompat.getDrawable(context, drawableRes), context.getString(label));
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public CharSequence getLabel() {
        return label;
    }
}
