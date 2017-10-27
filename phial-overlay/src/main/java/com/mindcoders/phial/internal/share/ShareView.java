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
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.mindcoders.phial.PageView;
import com.mindcoders.phial.R;
import com.mindcoders.phial.ShareContext;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.util.Precondition;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public class ShareView extends FrameLayout implements ShareContext, PageView {
    private final GridView contentGV;
    private final EditText messageTV;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;
    private final ViewSwitcher viewSwitcher;
    private final ProgressBar progressBar;

    @VisibleForTesting
    public ShareView(@NonNull Context context) {
        this(context, null, null);
        Precondition.calledFromTools(this);
    }

    public ShareView(@NonNull Context context, ShareManager shareManager, AttachmentManager attachmentManager) {
        super(context);
        this.shareManager = shareManager;
        this.attachmentManager = attachmentManager;

        LayoutInflater.from(context).inflate(R.layout.view_share, this, true);
        progressBar = findViewById(R.id.progress);
        viewSwitcher = findViewById(R.id.view_switcher);
        contentGV = findViewById(R.id.content);
        messageTV = findViewById(R.id.message);

        if (!isInEditMode()) {
            setupAdapter(context);
        }
    }

    private void setupAdapter(@NonNull Context context) {
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
            shareManager.share(shareItem, this, attachment, message);
        } catch (Exception e) {
            PhialErrorPlugins.onError(e);
            Toast.makeText(getContext(), R.string.share_error_attachment, Toast.LENGTH_SHORT).show();
        }
    }

    private void close() {
        Precondition.notImplemented("Close", getContext());
    }

    @Override
    public Context getAndroidContext() {
        return getContext();
    }

    @Override
    public void onSuccess() {
        moveToInitialStateIfNeeded();
        close();
    }

    @Override
    public void onFailed(String message) {
        moveToInitialStateIfNeeded();
    }

    private void moveToInitialStateIfNeeded() {
        if (isSubViewAdded()) {
            viewSwitcher.setDisplayedChild(0);
            viewSwitcher.removeViews(1, viewSwitcher.getChildCount());
        }
    }

    @Override
    public void presentView(View view) {
        if (isSubViewAdded()) {
            throw new IllegalArgumentException("can't present multiple views. Only single instance should be presented");
        }

        viewSwitcher.addView(view, new ViewSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        viewSwitcher.setDisplayedChild(1);
    }

    @Override
    public void setProgressBarVisibility(boolean isVisible) {
        progressBar.setVisibility(isVisible ? VISIBLE : GONE);
    }

    private boolean isSubViewAdded() {
        final int childCount = viewSwitcher.getChildCount();
        return childCount >= 2;
    }
}
