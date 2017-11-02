package com.mindcoders.phial.internal.share.attachment;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.ListAttacher;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 11/1/17.
 */

public class AttacherAdaptor implements ListAttacher {
    private final Attacher attacher;

    public AttacherAdaptor(Attacher attacher) {
        this.attacher = attacher;
    }

    public static ListAttacher adapt(Attacher attacher) {
        return new AttacherAdaptor(attacher);
    }

    @Override
    public List<File> provideAttachment() throws Exception {
        final File file = attacher.provideAttachment();
        if (file != null) {
            return Collections.singletonList(file);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onPreDebugWindowCreated() {
        attacher.onPreDebugWindowCreated();
    }

    @Override
    public void onAttachmentNotNeeded() {
        attacher.onAttachmentNotNeeded();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AttacherAdaptor that = (AttacherAdaptor) o;

        return attacher.equals(that.attacher);

    }

    @Override
    public int hashCode() {
        return attacher.hashCode();
    }
}
