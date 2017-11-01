package com.mindcoders.phial.jira;

import com.mindcoders.phial.internal.util.Precondition;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;

/**
 * Created by rost on 10/29/17.
 */

class JiraShareManager {
    interface ResultCallback {
        void onSuccess(String issueName);

        void onFail(Throwable th);
    }

    private final CredentialStore credentialStore;
    private final OkHttpFactory okHttpFactory;
    private final String baseUrl;
    private final String projectKey;
    private final Map<String, Object> extrtaProperties;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    JiraShareManager(CredentialStore credentialStore,
                     OkHttpFactory okHttpFactory,
                     String baseUrl, String projectKey,
                     Map<String, Object> extraProperties) {
        this.credentialStore = credentialStore;
        this.okHttpFactory = okHttpFactory;
        this.baseUrl = baseUrl;
        this.projectKey = projectKey;
        this.extrtaProperties = extraProperties;
    }

    boolean isAuthorized() {
        return credentialStore.hasCredentials();
    }

    void share(File attachment, String message, ResultCallback resultCallback) {
        Precondition.isTrue(isAuthorized(), "call of share when not authorized");
        final OkHttpClient client = okHttpFactory.createAuthorizedClient(credentialStore.getUserCredential());
        final JiraApi api = new JiraApi(client, baseUrl, projectKey);

        final HashMap<String, Object> extras = createExtras(message);
        executor.submit(new ShareJob(api, attachment, extras, resultCallback));
    }

    private HashMap<String, Object> createExtras(String message) {
        final HashMap<String, Object> extras = new HashMap<>(extrtaProperties);
        RestModelConverter.appendSummary(message, extras);
        return extras;
    }

    void authorize(String login, String password) {
        final String credentials = Credentials.basic(login, password);
        credentialStore.storeCredential(credentials);
    }


    private class ShareJob implements Runnable {
        private final JiraApi api;
        private final File attachment;
        private final Map<String, Object> properties;
        private final ResultCallback resultCallback;

        ShareJob(JiraApi api, File attachment, Map<String, Object> properties, ResultCallback resultCallback) {
            this.api = api;
            this.attachment = attachment;
            this.properties = properties;
            this.resultCallback = resultCallback;
        }

        @Override
        public void run() {
            try {
                final CreatedIssueResponse createdIssue = api.createIssue(properties);
                api.attachFile(createdIssue.getKey(), attachment);
                resultCallback.onSuccess(createdIssue.getKey());
            } catch (ApiException apiException) {
                if (apiException.getCode() == 401 || apiException.getCode() == 403) {
                    credentialStore.drop();
                }
                resultCallback.onFail(apiException);
            } catch (Throwable th) {
                resultCallback.onFail(th);
            }
        }
    }
}
