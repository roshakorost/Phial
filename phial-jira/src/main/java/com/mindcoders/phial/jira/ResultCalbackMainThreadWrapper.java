package com.mindcoders.phial.jira;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by rost on 10/29/17.
 */

class ResultCalbackMainThreadWrapper {
    JiraShareManager.ResultCallback wrap(final JiraShareManager.ResultCallback resultCallback) {
        final Handler handler = new Handler(Looper.getMainLooper());
        return new JiraShareManager.ResultCallback() {
            @Override
            public void onSuccess(final String issueName) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resultCallback.onSuccess(issueName);
                    }
                });
            }

            @Override
            public void onFail(final Throwable th) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onFail(th);
                    }
                });
            }
        };
    }
}
