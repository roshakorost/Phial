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

import com.mindcoders.phial.R;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public class ShareView extends FrameLayout {
    private final RecyclerView contentRV;
    private final EditText messageTV;

    private final ShareAdapter adapter;

    public ShareView(@NonNull Context context){
        this(context, null);
    }

    public ShareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShareView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_share, this, true);
        contentRV = findViewById(R.id.content);
        messageTV = findViewById(R.id.message);

        adapter = new ShareAdapter(LayoutInflater.from(context));

        contentRV.setLayoutManager(new GridLayoutManager(context, 4));
        contentRV.setAdapter(adapter);
    }

    public void setFiles(List<File> attachment) {
        final ShareManager shareManager = ShareManager.getInstance();
        final List<ShareItem> shareables = shareManager.getShareables();

        adapter.swapData(shareables);
    }

}
