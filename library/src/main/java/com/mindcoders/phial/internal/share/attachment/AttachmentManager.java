package com.mindcoders.phial.internal.share.attachment;

import com.mindcoders.phial.AttachmentProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */
public class AttachmentManager {
    private final List<AttachmentProvider> providers;

    public AttachmentManager(List<AttachmentProvider> providers) {
        this.providers = providers;
    }

    public List<File> prepareAttachments() throws Exception {
        final List<File> result = new ArrayList<>(providers.size());
        for (AttachmentProvider provider : providers) {
            result.add(provider.provideAttachment());
        }
        return result;
    }
}
