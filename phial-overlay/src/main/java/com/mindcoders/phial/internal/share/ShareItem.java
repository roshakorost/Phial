package com.mindcoders.phial.internal.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.annotation.VisibleForTesting;

import com.mindcoders.phial.ShareDescription;
import com.mindcoders.phial.Shareable;

import static android.support.annotation.VisibleForTesting.PACKAGE_PRIVATE;

/**
 * Created by rost on 10/23/17.
 */
@VisibleForTesting(otherwise = PACKAGE_PRIVATE)
public abstract class ShareItem {
    private final ShareDescription description;

    static ShareItem create(Shareable shareable) {
        return new UserShareItem(shareable);
    }

    static ShareItem create(Context context, ResolveInfo resolveInfo) {
        final PackageManager pm = context.getPackageManager();
        final CharSequence label = resolveInfo.loadLabel(pm);
        final Drawable drawable = resolveInfo.loadIcon(pm);

        final ShareDescription shareDescription = new ShareDescription(drawable, label);
        final ComponentName componentName = new ComponentName(
                resolveInfo.activityInfo.packageName,
                resolveInfo.activityInfo.name
        );
        return new SystemShareItem(shareDescription, componentName);
    }

    ShareItem(ShareDescription description) {
        this.description = description;
    }

    ShareDescription getDescription() {
        return description;
    }
}

