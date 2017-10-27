package com.mindcoders.phial.jira;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.mindcoders.phial.ShareContext;
import com.mindcoders.phial.ShareDescription;
import com.mindcoders.phial.Shareable;

import java.io.File;

/**
 * Created by rost on 10/27/17.
 */

public class JiraShareable implements Shareable {
    private final Context context;

    public JiraShareable(Context context) {
        this.context = context;
    }

    @Override
    public void share(ShareContext shareContext, File attachment, String message) {
        final LayoutInflater inflater = LayoutInflater.from(shareContext.getAndroidContext());
        View view = inflater.inflate(R.layout.jira_login_view, null, false);
        shareContext.presentView(view);
    }

    @Override
    public ShareDescription getDescription() {
        return new ShareDescription(context.getResources().getDrawable(R.drawable.ic_jira), context.getString(R.string.jira_name));
    }
}
