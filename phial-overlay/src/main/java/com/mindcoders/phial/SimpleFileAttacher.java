package com.mindcoders.phial;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 11/1/17.
 */

public class SimpleFileAttacher implements ListAttacher {
    private final List<File> files;

    public SimpleFileAttacher(Collection<File> files) {
        this.files = new ArrayList<>(files);
    }

    public SimpleFileAttacher(File file) {
        this.files = Collections.singletonList(file);
    }

    @Override
    public List<File> provideAttachment() throws Exception {
        return files;
    }

    @Override
    public void onPreDebugWindowCreated() {

    }

    @Override
    public void onAttachmentNotNeeded() {

    }
}
