package com.mindcoders.phial;

import java.io.File;

/**
 * Shareables are sued to add custom share options to ShareWindow of Phial
 * <p>
 * use {@link PhialBuilder#addShareable(Shareable)} in order to add custom sharable
 */
public interface Shareable {
    /**
     * Is called when user selects provided share option.
     * <p>
     * At the end of share should notify Phial about result of sharing by calling one of
     * {@link ShareContext#onSuccess()}
     * {@link ShareContext#onFailed(String)}
     * {@link ShareContext#onCancel()}
     * <p>
     * ShareContext can be used in order to show your custom view or progress when user selected your share option
     * see {@link ShareContext}
     *
     * @param shareContext     used in order to communicate with Phial
     * @param zippedAttachment file that should be shared
     * @param message          message provided by user
     */
    void share(ShareContext shareContext, File zippedAttachment, String message);

    /**
     * Sets icon and title for ShareOption
     *
     * @return ShareDescription with icon and title
     */
    ShareDescription getDescription();
}
