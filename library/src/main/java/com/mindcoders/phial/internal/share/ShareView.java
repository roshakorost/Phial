package com.mindcoders.phial.internal.share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.share.ShareAdapter.OnItemClickedListener;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.util.GridSpacingItemDecoration;
import com.mindcoders.phial.internal.util.Precondition;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public class ShareView extends FrameLayout {
    private static final int COLUMN_COUNT = 4;

    private final RecyclerView contentRV;
    private final EditText messageTV;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;

    private final OnItemClickedListener<ShareItem> clickedListener = new OnItemClickedListener<ShareItem>() {
        @Override
        public void onItemClicked(ShareItem item) {
            shareItem(item);
        }
    };

    @VisibleForTesting
    public ShareView(@NonNull Context context) {
        super(context);
        Precondition.calledFromTools(this);
        contentRV = null;
        messageTV = null;
        shareManager = null;
        attachmentManager = null;
    }

    public ShareView(@NonNull Context context, ShareManager shareManager, AttachmentManager attachmentManager) {
        super(context);
        this.shareManager = shareManager;
        this.attachmentManager = attachmentManager;

        LayoutInflater.from(context).inflate(R.layout.view_share, this, true);
        contentRV = findViewById(R.id.content);
        messageTV = findViewById(R.id.message);

        contentRV.setLayoutManager(new GridLayoutManager(context, COLUMN_COUNT));

        final int itemsPaddin = getResources().getDimensionPixelSize(R.dimen.share_padding_items);
        contentRV.addItemDecoration(new GridSpacingItemDecoration(COLUMN_COUNT, itemsPaddin, false));

        final List<ShareItem> shareables = this.shareManager.getShareables();
        final ShareAdapter adapter = new ShareAdapter(shareables);
        adapter.setClickedListener(clickedListener);
        contentRV.setAdapter(adapter);
    }

    private void shareItem(ShareItem shareItem) {
        try {
            final File attachment = attachmentManager.createAttachment();
            final String message = messageTV.getText().toString();
            shareManager.share(shareItem, attachment, message);
        } catch (Exception e) {
            PhialErrorPlugins.onError(e);
            Toast.makeText(getContext(), R.string.share_error_attachment, Toast.LENGTH_SHORT).show();
        }
    }
}
