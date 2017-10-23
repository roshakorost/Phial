package com.mindcoders.phial.internal.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.mindcoders.phial.Shareable;
import com.mindcoders.phial.internal.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public class ShareManager {
    private final Context context;
    private final List<ShareItem> userShareItems;

    public ShareManager(Context context, List<Shareable> userShareables) {
        this.context = context;
        this.userShareItems = createUserShareItem(userShareables);
    }

    Intent createShareIntent(Uri file, String message) {
        final Intent intent = new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("application/zip")
                .putExtra(Intent.EXTRA_TEXT, message);

        if (file != null) {
            intent.putExtra(Intent.EXTRA_STREAM, file);
        }

        return intent;
    }

    List<ShareItem> getShareables() {
        final Intent shareIntent = createShareIntent(null, "dummy message");

        final List<ResolveInfo> infos = context.getPackageManager().queryIntentActivities(shareIntent, 0);

        final List<ShareItem> result = new ArrayList<>(userShareItems.size() + infos.size());
        result.addAll(userShareItems);
        result.addAll(createSystemShareItems(infos));

        return result;
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
