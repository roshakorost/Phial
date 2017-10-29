package com.mindcoders.phial.internal.share;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.mindcoders.phial.PageView;
import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.util.Precondition;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public class ShareView extends FrameLayout implements PageView {

    private final GridView contentGV;
    private final EditText messageTV;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;

    @VisibleForTesting
    public ShareView(@NonNull Context context) {
        super(context);
        Precondition.calledFromTools(this);
        contentGV = null;
        messageTV = null;
        shareManager = null;
        attachmentManager = null;
    }

    public ShareView(@NonNull Context context, ShareManager shareManager, AttachmentManager attachmentManager) {
        super(context);
        this.shareManager = shareManager;
        this.attachmentManager = attachmentManager;

        LayoutInflater.from(context).inflate(R.layout.view_share, this, true);
        contentGV = findViewById(R.id.content);
        messageTV = findViewById(R.id.message);

        final List<ShareItem> shareables = this.shareManager.getShareables();
        final ShareAdapter adapter = new ShareAdapter(context, shareables);
        contentGV.setAdapter(adapter);
        contentGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ShareItem item = adapter.getItem(position);
                shareItem(item);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
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
