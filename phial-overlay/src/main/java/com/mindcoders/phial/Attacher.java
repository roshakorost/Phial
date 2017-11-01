package com.mindcoders.phial;

import android.support.annotation.Nullable;

import java.io.File;

/**
 * Created by rost on 10/22/17.
 */

public interface Attacher {
    /**
     * Provides phial with attachment that will be associated with debug data.
     * If method returns directory all files from it would be attached to debug data.
     *
     * if method returns null current attachment will be ignore.
     */
    @Nullable
    File provideAttachment() throws Exception;

    void onPreDebugWindowCreated();

    void onAttachmentNotNeeded();
}
