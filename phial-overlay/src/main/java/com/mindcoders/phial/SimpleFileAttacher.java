package com.mindcoders.phial;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Attacher can be used in order to include some files in Phial Share attachment.
 * Should be used if you now the filepath of target files during creation of Phial. E.g. adding shared prefs or
 * SQLite database
 */
public class SimpleFileAttacher implements ListAttacher {
    private final List<File> files;

    /**
     * @param files that should be included in share attachment. If some file is directory
     *              all files form it will be included
     */
    public SimpleFileAttacher(Collection<File> files) {
        this.files = new ArrayList<>(files);
    }

    /**
     * @param file that should be included in share attachment. If it is directory all files form it will be included
     */
    public SimpleFileAttacher(File file) {
        this.files = Collections.singletonList(file);
    }

    @Override
    public List<File> provideAttachment() throws Exception {
        return files;
    }

    @Override
    public void onPreDebugWindowCreated() {
        //optional method for implementation.
    }

    @Override
    public void onAttachmentNotNeeded() {
        //optional method for implementation.
    }
}
