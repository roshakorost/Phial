package com.mindcoders.phial.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by rost on 10/23/17.
 */
abstract class ShareItem {
    private final ShareDescription description;

    static ShareItem create(Shareable shareable) {
        return new UserShareItem(shareable);
    }

    static ShareItem create(Context context, ResolveInfo resolveInfo) {
        final PackageManager pm = context.getPackageManager();
        final CharSequence label = resolveInfo.loadLabel(pm);
        final Drawable drawable = resolveInfo.loadIcon(pm);

        final ShareDescription shareDescription = new ShareDescription(drawable, label);
        final ComponentName componentName = new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        return new SystemShareItem(shareDescription, componentName);
    }

    ShareItem(ShareDescription description) {
        this.description = description;
    }

    ShareDescription getDescription() {
        return description;
    }
}

class UserShareItem extends ShareItem {
    private final Shareable shareable;

    UserShareItem(Shareable shareable) {
        super(shareable.getDescription());
        this.shareable = shareable;
    }

    public Shareable getShareable() {
        return shareable;
    }
}

class SystemShareItem extends ShareItem {
    private final ComponentName componentName;

    SystemShareItem(ShareDescription shareDescription, ComponentName componentName) {
        super(shareDescription);
        this.componentName = componentName;
    }

    ComponentName getComponentName() {
        return componentName;
    }
}
