package com.mindcoders.phial;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;

import java.io.File;

/**
 * Used for communication with Phial when share option is selected see {@link Shareable#share(ShareContext, File, String)}
 * <p>
 * At the end of share should notify Phial about result of sharing by calling one of
 * {@link #onSuccess()}
 * {@link #onFailed(String)}
 * {@link #onCancel()}
 */
public interface ShareContext {
    /**
     * @return Android Context in which Phial works
     */
    Context getAndroidContext();

    /**
     * Should be called if successfully shared
     */
    void onSuccess();

    /**
     * Should be called in case of error during share.
     *
     * @param message that will be presented to user
     */
    void onFailed(String message);

    /**
     * Should be called in case user cancel share
     */
    void onCancel();

    /**
     * Presents custom view that will be shown to user
     *
     * @param view view to present
     */
    void presentView(View view);

    /**
     * Shows or hides progress bar to user
     *
     * @param isVisible true will show progressbar false will hide
     */
    void setProgressBarVisibility(boolean isVisible);

    /**
     * Creates view using provided layoutId. Created view will not be presented.
     * Should {@link #presentView(View)} in order to show to user
     *
     * @param layoutId Android layout resource
     * @return created view
     */
    View inflate(@LayoutRes int layoutId);
}
