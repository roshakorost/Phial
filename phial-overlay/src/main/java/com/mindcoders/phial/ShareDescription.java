package com.mindcoders.phial;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.mindcoders.phial.internal.util.support.ContextCompat;


/**
 * Description of share option that is shown under Share section of Phial.
 * Should contain icon and title
 * <p>
 * see {@link Shareable}
 */
public class ShareDescription {
    private final Drawable drawable;
    private final CharSequence label;

    /**
     * @param drawable icon that will be displayed with share option
     * @param label    label for share option
     */
    public ShareDescription(Drawable drawable, CharSequence label) {
        this.drawable = drawable;
        this.label = label;
    }

    /**
     * @param context     android context that will be used to load icon and title
     * @param drawableRes drawableId that will be displayed with share option
     * @param label       labelId for share option
     */
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
