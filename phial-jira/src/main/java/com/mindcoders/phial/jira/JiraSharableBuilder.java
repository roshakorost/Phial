package com.mindcoders.phial.jira;

import android.content.Context;

import com.mindcoders.phial.ShareDescription;
import com.mindcoders.phial.Shareable;
import com.mindcoders.phial.internal.util.Precondition;

/**
 * Created by rost on 10/29/17.
 */

public class JiraSharableBuilder {
    private final Context context;
    private ShareDescription shareDescription;
    private String baseUrl;
    private String projectKey;

    public JiraSharableBuilder(Context context) {
        this.context = context;
        shareDescription = new ShareDescription(
                context.getResources().getDrawable(R.drawable.ic_jira),
                context.getString(R.string.jira_name)
        );
    }

    public JiraSharableBuilder setShareDescription(ShareDescription shareDescription) {
        Precondition.notNull(shareDescription, "shareDescription should not be null");
        this.shareDescription = shareDescription;
        return this;
    }

    public JiraSharableBuilder setBaseUrl(String baseUrl) {
        Precondition.notNull(baseUrl, "baseUrl should not be null");
        this.baseUrl = baseUrl;
        return this;
    }

    public JiraSharableBuilder setProjectKey(String projectKey) {
        Precondition.notNull(projectKey, "projectKey should not be null");
        this.projectKey = projectKey;
        return this;
    }

    public Shareable build() {
        final CredentialStore store = new CredentialStore(context);
        final JiraShareManager shareManager = new JiraShareManager(store, new OkHttpFactory(), baseUrl, projectKey);
        return new JiraShareable(shareDescription, shareManager);
    }
}
