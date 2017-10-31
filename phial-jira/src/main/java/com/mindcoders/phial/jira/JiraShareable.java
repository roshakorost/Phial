package com.mindcoders.phial.jira;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mindcoders.phial.ShareContext;
import com.mindcoders.phial.ShareDescription;
import com.mindcoders.phial.Shareable;

import java.io.File;

/**
 * Created by rost on 10/27/17.
 */

class JiraShareable implements Shareable {
    private final ShareDescription description;
    private final JiraShareManager shareManager;

    JiraShareable(ShareDescription description, JiraShareManager shareManager) {
        this.description = description;
        this.shareManager = shareManager;
    }

    @Override
    public void share(final ShareContext shareContext, File attachment, String message) {
        if (shareManager.isAuthorized()) {
            internalShare(shareContext, attachment, message);
        } else {
            presentLoginDialog(shareContext, attachment, message);
        }
    }

    private void presentLoginDialog(final ShareContext shareContext, final File attachment, final String message) {
        final View view = shareContext.inflate(R.layout.jira_login_view);
        final EditText loginTV = view.findViewById(R.id.login);

        final EditText passwordTV = view.findViewById(R.id.password);
        view.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContext.onCancel();
            }
        });

        view.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareManager.authorize(loginTV.getText().toString(), passwordTV.getText().toString());
                internalShare(shareContext, attachment, message);
            }
        });

        passwordTV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO | actionId == EditorInfo.IME_ACTION_DONE) {
                    shareManager.authorize(loginTV.getText().toString(), passwordTV.getText().toString());
                    internalShare(shareContext, attachment, message);
                    return true;
                }

                return false;
            }
        });

        shareContext.presentView(view);
    }

    private void internalShare(ShareContext shareContext, File attachment, String message) {
        shareContext.setProgressBarVisibility(true);
        final JiraShareManager.ResultCallback callback = ResultCalbackMainThreadWrapper.wrap(new ShareResult(shareContext));
        shareManager.share(attachment, message, callback);
    }

    private static class ShareResult implements JiraShareManager.ResultCallback {
        private final ShareContext context;

        private ShareResult(ShareContext context) {
            this.context = context;
        }

        @Override
        public void onSuccess(String issueName) {
            context.setProgressBarVisibility(false);
            final Context androidContext = context.getAndroidContext();
            Toast.makeText(
                    androidContext,
                    androidContext.getString(R.string.jira_issue_created, issueName),
                    Toast.LENGTH_SHORT
            ).show();
            context.onSuccess();
        }

        @Override
        public void onFail(Throwable th) {
            context.setProgressBarVisibility(false);
            context.onFailed(th.getMessage());
        }
    }

    @Override
    public ShareDescription getDescription() {
        return description;
    }

}
