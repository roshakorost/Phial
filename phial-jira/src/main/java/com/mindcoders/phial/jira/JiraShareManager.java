package com.mindcoders.phial.jira;

import com.mindcoders.phial.internal.util.Precondition;

import java.io.File;
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
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    JiraShareManager(CredentialStore credentialStore, OkHttpFactory okHttpFactory, String baseUrl, String projectKey) {
        this.credentialStore = credentialStore;
        this.okHttpFactory = okHttpFactory;
        this.baseUrl = baseUrl;
        this.projectKey = projectKey;
    }

    boolean isAuthorized() {
        return credentialStore.hasCredentials();
    }

    void share(File attachment, String message, ResultCallback resultCallback) {
        Precondition.isTrue(isAuthorized(), "call of share when not authorized");
        final OkHttpClient client = okHttpFactory.createAuthorizedClient(credentialStore.getUserCredential());
        final JiraApi api = new JiraApi(client, baseUrl, projectKey);
        executor.submit(new ShareJob(api, attachment, message, resultCallback));
    }

    void authorize(String login, String password) {
        final String credentials = Credentials.basic(login, password);
        credentialStore.storeCredential(credentials);
    }


    private class ShareJob implements Runnable {
        private final JiraApi api;
        private final File attachment;
        private final String message;
        private final ResultCallback resultCallback;

        ShareJob(JiraApi api, File attachment, String message, ResultCallback resultCallback) {
            this.api = api;
            this.attachment = attachment;
            this.message = message;
            this.resultCallback = resultCallback;
        }

        @Override
        public void run() {
            try {
                final CreatedIssueResponse createdIssue = api.createIssue(message);
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
