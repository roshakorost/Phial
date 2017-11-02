package com.mindcoders.phial.internal.keyvalue;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.keyvalue.Category;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rost on 10/30/17.
 */

public final class BuildInfoWriter extends InfoWriter {
    private final static String DATE_FORMAT = "d MMM yyyy HH:mm Z";
    @Nullable
    private final Long buildTime;
    @Nullable
    private final String buildCommit;

    public BuildInfoWriter(Context context) {
        this(context, null, null);
    }

    public BuildInfoWriter(Context context, @Nullable Long buildTime, @Nullable String buildCommit) {
        super(context, "Build");
        this.buildTime = buildTime;
        this.buildCommit = buildCommit;
    }

    @Override
    protected void writeInfo(Category category) {
        final String packageName = getContext().getPackageName();
        category.setKey("Package", packageName);

        try {
            final PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_META_DATA);
            category.setKey("Version", packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            PhialErrorPlugins.onError(e);
        }

        if (buildTime != null) {
            category.setKey("BuildTime", getBuildTime(buildTime));
        }

        if (buildCommit != null) {
            category.setKey("BuildCommit", buildCommit);
        }
    }


    private static String getBuildTime(long buildTime) {
        final Date buildDate = new Date(buildTime);
        return new SimpleDateFormat(DATE_FORMAT, Locale.US).format(buildDate);
    }
}
