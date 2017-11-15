package com.mindcoders.phial.internal.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.mindcoders.phial.ShareContext;
import com.mindcoders.phial.Shareable;
import com.mindcoders.phial.internal.util.CollectionUtils;
import com.mindcoders.phial.internal.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public class ShareManager {
    private final Context context;
    private final String authority;
    private final List<ShareItem> userShareItems;

    public ShareManager(Context context, String authority, List<Shareable> userShareables) {
        this.context = context;
        this.authority = authority;
        this.userShareItems = createUserShareItem(userShareables);
    }

    List<ShareItem> getShareables() {
        final Intent shareIntent = createShareIntent(null, "dummy message");

        final List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(shareIntent, 0);

        final List<ShareItem> result = new ArrayList<>(userShareItems.size() + infos.size());
        result.addAll(userShareItems);
        result.addAll(createSystemShareItems(infos));

        return result;
    }

    void share(ShareItem shareItem, ShareContext shareContext, File attachment, String message) {
        if (shareItem instanceof SystemShareItem) {
            share((SystemShareItem) shareItem, shareContext, attachment, message);
        } else if (shareItem instanceof UserShareItem) {
            share((UserShareItem) shareItem, shareContext, attachment, message);
        } else {
            throw new IllegalArgumentException("unexpected share item type " + shareItem);
        }
    }

    void share(SystemShareItem shareItem, ShareContext shareContext, File attachment, String message) {
        final Uri uri = FileUtil.getUri(context, authority, attachment);
        final Intent shareIntent = createShareIntent(uri, message);
        shareIntent.setComponent(shareItem.getComponentName());
        context.startActivity(shareIntent);
        shareContext.onSuccess();
    }

    void share(UserShareItem shareItem, ShareContext shareContext, File attachment, String message) {
        shareItem.getShareable().share(shareContext, attachment, message);
    }

    private Intent createShareIntent(Uri file, String message) {
        final Intent intent = new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setType("application/zip")
                .putExtra(Intent.EXTRA_TEXT, message);

        if (file != null) {
            intent.putExtra(Intent.EXTRA_STREAM, file);
        }

        return intent;
    }

    private ArrayList<ShareItem> createSystemShareItems(List<ResolveInfo> infos) {
        return CollectionUtils.map(infos, new CollectionUtils.Function1<ShareItem, ResolveInfo>() {
            @Override
            public ShareItem call(ResolveInfo info) {
                return ShareItem.create(context, info);
            }
        });
    }

    private static List<ShareItem> createUserShareItem(List<Shareable> userShareables) {
        return Collections.unmodifiableList(
                CollectionUtils.map(userShareables, new CollectionUtils.Function1<ShareItem, Shareable>() {
                    @Override
                    public ShareItem call(Shareable shareable) {
                        return ShareItem.create(shareable);
                    }
                })
        );
    }
}
