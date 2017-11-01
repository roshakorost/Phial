package com.mindcoders.phial;

import java.io.File;

/**
 * Created by rost on 10/22/17.
 */

public interface Shareable {
    void share(ShareContext shareContext, File zippedAttachment, String message);

    ShareDescription getDescription();
}
