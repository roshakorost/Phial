package com.mindcoders.phial;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 11/1/17.
 */

public interface ListAttacher {
    /**
     * Provides phial with attachment that will be associated with debug data.
     * If method returns directory all files from it would be attached to debug data.
     */
    List<File> provideAttachment() throws Exception;

    void onPreDebugWindowCreated();

    void onAttachmentNotNeeded();
}
