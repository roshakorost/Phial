package com.mindcoders.phial;

import java.io.File;
import java.util.List;

/**
 * Attacher can be used in order to include some files in Phial Share attachment.
 * <p>
 * When user selects some share option Phial iterates through attachers and requests creation of attachment
 * <p>
 * use {@link PhialBuilder#addAttachmentProvider(ListAttacher)} in order to add
 */
public interface ListAttacher {
    /**
     * Provides phial with attachment that will be associated with debug data.
     * If file is directory all files from it would be attached to debug data.
     *
     * @return list of files that should be included in Phial share attachment
     */
    List<File> provideAttachment() throws Exception;

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
