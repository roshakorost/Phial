package com.mindcoders.phial.internal.share;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mindcoders.phial.R;
import com.mindcoders.phial.internal.PhialCore;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.share.ShareAdapter.OnItemClickedListener;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public class ShareView extends FrameLayout {
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

    public ShareView(@NonNull Context context) {
        this(context, null);
    }

    public ShareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shareManager = PhialCore.getInstance().getShareManager();
        attachmentManager = PhialCore.getInstance().getAttachmentManager();

        LayoutInflater.from(context).inflate(R.layout.view_share, this, true);
        contentRV = findViewById(R.id.content);
        messageTV = findViewById(R.id.message);

        contentRV.setLayoutManager(new GridLayoutManager(context, 4));
        final List<ShareItem> shareables = shareManager.getShareables();
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
