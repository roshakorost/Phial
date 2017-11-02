package com.mindcoders.phial.jira;

import android.content.Context;

import com.mindcoders.phial.ShareDescription;
import com.mindcoders.phial.Shareable;
import com.mindcoders.phial.internal.util.Precondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rost on 10/29/17.
 */

public class JiraShareableBuilder {
    private final Context context;
    private ShareDescription shareDescription;
    private String baseUrl;
    private String projectKey;
    private final Map<String, Object> extraProperties = new HashMap<>();

    public JiraShareableBuilder(Context context) {
        this.context = context;
        shareDescription = new ShareDescription(
                context.getResources().getDrawable(R.drawable.ic_jira),
                context.getString(R.string.jira_name)
        );
    }

    public JiraShareableBuilder setShareDescription(ShareDescription shareDescription) {
        Precondition.notNull(shareDescription, "shareDescription should not be null");
        this.shareDescription = shareDescription;
        return this;
    }

    public JiraShareableBuilder setBaseUrl(String baseUrl) {
        Precondition.notNull(baseUrl, "baseUrl should not be null");
        this.baseUrl = baseUrl;
        return this;
    }

    public JiraShareableBuilder setProjectKey(String projectKey) {
        Precondition.notNull(projectKey, "projectKey should not be null");
        this.projectKey = projectKey;
        return this;
    }

    public JiraShareableBuilder setCustomField(String key, Object value) {
        Precondition.notNull(key, "key should not be null");
        Precondition.notNull(value, "value should not be null");
        this.extraProperties.put(key, value);
        return this;
    }

    public Shareable build() {
        final CredentialStore store = new CredentialStore(context);
        final JiraShareManager shareManager = new JiraShareManager(store, new OkHttpFactory(), baseUrl, projectKey, extraProperties);
        return new JiraShareable(shareDescription, shareManager);
    }

    public JiraShareableBuilder setFixVersions(String... versions) {
        final List<Map> objects = new ArrayList<>(versions.length);
        for (String version : versions) {
            Map versionObj = Collections.singletonMap("name", version);
            objects.add(versionObj);
        }
        return setCustomField("fixVersions", objects);
    }

    public JiraShareableBuilder setAffectsVersions(String... versions) {
        final List<Map> objects = new ArrayList<>(versions.length);
        for (String version : versions) {
            Map versionObj = Collections.singletonMap("name", version);
            objects.add(versionObj);
        }
        return setCustomField("versions", objects);
    }
}
