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
@SuppressWarnings("checkstyle:JavadocMethod")
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

    /**
     * Set the URL of Jira.
     * <p>
     * Required
     *
     * @param baseUrl jira base url
     * @return same instance of builder
     */
    public JiraShareableBuilder setBaseUrl(String baseUrl) {
        Precondition.notNull(baseUrl, "baseUrl should not be null");
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Set project key. Issues will be created in project with specified key.
     * <p>
     * Required
     *
     * @param projectKey project key of Jira project.
     * @return
     */
    public JiraShareableBuilder setProjectKey(String projectKey) {
        Precondition.notNull(projectKey, "projectKey should not be null");
        this.projectKey = projectKey;
        return this;
    }

    /**
     * Changes icon and title for Jira item in ShareSection.
     * <p>
     * Optional
     *
     * @param shareDescription title and icon that should be used
     * @return same instance of builder
     */
    public JiraShareableBuilder setShareDescription(ShareDescription shareDescription) {
        Precondition.notNull(shareDescription, "shareDescription should not be null");
        this.shareDescription = shareDescription;
        return this;
    }

    /**
     * Set extra fields for created issue.
     * Fields will be created under fields section.
     * <p>
     * See https://docs.atlassian.com/jira/REST/cloud/#api/2/issue-createIssue
     * e.g. You want to add `description` field so you can use
     * setCustomField("description", "your description")
     * <p>
     * In case you need to include complex JSON object to fields, you may create JSONObject or Map
     * and pass it as value
     * <p>
     * Optioanl
     *
     * @param key   Jira fields key
     * @param value associated value
     * @return same instance of builder
     */
    public JiraShareableBuilder setCustomField(String key, Object value) {
        Precondition.notNull(key, "key should not be null");
        Precondition.notNull(value, "value should not be null");
        this.extraProperties.put(key, value);
        return this;
    }

    /**
     * Sets fixVersions for create issue
     * <p>
     * Optioanl
     *
     * @param versions valid Jira versions
     * @return same instance of builder
     */
    public JiraShareableBuilder setFixVersions(String... versions) {
        final List<Map> objects = new ArrayList<>(versions.length);
        for (String version : versions) {
            Map versionObj = Collections.singletonMap("name", version);
            objects.add(versionObj);
        }
        return setCustomField("fixVersions", objects);
    }

    /**
     * Sets fixVersions for create issue
     * <p>
     * Optioanl
     *
     * @param versions valid Jira versions
     * @return same instance of builder
     */
    public JiraShareableBuilder setAffectsVersions(String... versions) {
        final List<Map> objects = new ArrayList<>(versions.length);
        for (String version : versions) {
            Map versionObj = Collections.singletonMap("name", version);
            objects.add(versionObj);
        }
        return setCustomField("versions", objects);
    }

    /**
     * Creates Shareable that should be included in PhialOverlay
     * {@link com.mindcoders.phial.PhialBuilder#addShareable(Shareable)}
     *
     * @return Sharable that provides Jira share option
     */
    public Shareable build() {
        final CredentialStore store = new CredentialStore(context);
        final JiraShareManager shareManager = new JiraShareManager(
                store,
                new OkHttpFactory(),
                baseUrl,
                projectKey,
                extraProperties
        );
        return new JiraShareable(shareDescription, shareManager);
    }
}
