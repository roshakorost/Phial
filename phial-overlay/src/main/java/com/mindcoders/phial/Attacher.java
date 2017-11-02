package com.mindcoders.phial;

import android.support.annotation.Nullable;

import java.io.File;

/**
 * Is used in order to include some files in Phial Share attachment.
 * <p>
 * When user selects some share option Phial iterates through attachers and requests creation of attachment
 */
public interface Attacher {
    /**
     * Provides phial with attachment that will be associated with debug data.
     * If method returns directory all files from it would be attached to debug data.
     * <p>
     * if method returns null current attachment will be ignore.
     * <p>
     * In order to attach multiple files use {@link ListAttacher}
     * Use {@link SimpleFileAttacher} if you now the target filepath during configuration of Phial
     *
     * @return file or directory that should be included in attachment. Return null if nothing to attach.
     */
    @Nullable
    File provideAttachment() throws Exception;

    /**
     * Is called before Phial debug window is shown. Attacher might want to take screen shots or perform other actions
     * before Phial debug window is shown
     */
    void onPreDebugWindowCreated();

    /**
     * Indicates when provided attachment is no longer used by Phial.
     * If there no other users of the attachment it can be deleted.
     */
    void onAttachmentNotNeeded();
}
