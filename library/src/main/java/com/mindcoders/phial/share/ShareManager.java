package com.mindcoders.phial.share;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.mindcoders.phial.util.CollectionUtils;
import com.mindcoders.phial.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

class ShareManager {
    private final Context context;
    private final List<ShareItem> userShareItems;
    private static ShareManager instance;

    static ShareManager getInstance() {
        if (instance == null) {
            instance = new ShareManager(null, null);
        }

        return instance;
    }

    ShareManager(Context context, List<Shareable> userShareables) {
        this.context = context;
        this.userShareItems = createUserShareItem(userShareables);
    }

    Intent createShareIntent(List<File> files, String message) {
        return new Intent(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .setType("*/*")
                .putExtra(Intent.EXTRA_TEXT, message)
                .putParcelableArrayListExtra(Intent.EXTRA_STREAM, getUris(files));
    }

    List<ShareItem> getShareables(List<File> files) {
        final Intent shareIntent = createShareIntent(files, "dummy message");

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

    private ArrayList<Uri> getUris(List<File> files) {
        return CollectionUtils.map(files, new CollectionUtils.Function1<Uri, File>() {
            @Override
            public Uri call(File file) {
                return FileUtil.getUriForFile(context, file);
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
