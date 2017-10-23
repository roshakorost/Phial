package com.mindcoders.phial.internal.share.attachment;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.internal.PhialErrorHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */
public class AttachmentManager {
    private final List<Attacher> providers;

    public AttachmentManager(List<Attacher> providers) {
        this.providers = providers;
    }

    public List<File> prepareAttachments() {
        final List<File> result = new ArrayList<>(providers.size());

        for (Attacher provider : providers) {
            try {
                result.add(provider.provideAttachment());
            } catch (Exception ex) {
                PhialErrorHandler.onError(ex);
            }
        }

        return result;
    }
}
