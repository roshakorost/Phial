package com.mindcoders.phial.internal;

import android.app.Application;

import com.mindcoders.phial.AttachmentProvider;
import com.mindcoders.phial.Shareable;

import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 10/23/17.
 */

public class PhialConfig {
    private final Application application;
    private final List<Shareable> userSharables;
    private final List<AttachmentProvider> attachmentProviders;

    public PhialConfig(Application application) {
        this(application, Collections.<Shareable>emptyList(), Collections.<AttachmentProvider>emptyList());
    }

    public PhialConfig(Application application, List<Shareable> userSharables, List<AttachmentProvider> attachmentProviders) {
        this.application = application;
        this.userSharables = userSharables;
        this.attachmentProviders = attachmentProviders;
    }

    public List<Shareable> getUserSharables() {
        return userSharables;
    }

    public List<AttachmentProvider> getAttachmentProviders() {
        return attachmentProviders;
    }

    public Application getApplication() {
        return application;
    }
}
