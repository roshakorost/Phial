package com.mindcoders.phial;

import android.app.Application;

import com.mindcoders.phial.internal.PhialCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by rost on 10/23/17.
 */

public class PhialBuilder {
    private final Application application;
    private final List<Shareable> shareables = new ArrayList<>();
    private final List<Attacher> attachers = new ArrayList<>();
    private boolean attachScreenShots = true;
    private boolean applySystemInfo = true;

    public PhialBuilder(Application application) {
        this.application = application;
    }

    public PhialBuilder addSharable(Shareable shareable) {
        this.shareables.add(shareable);
        return this;
    }

    public PhialBuilder addAtachmentProvider(Attacher attacher) {
        this.attachers.add(attacher);
        return this;
    }

    public PhialBuilder attachScreenShot(boolean attachScreenShot) {
        this.attachScreenShots = attachScreenShot;
        return this;
    }

    public PhialBuilder applySystemInfo(boolean applySystemInfo) {
        this.applySystemInfo = applySystemInfo;
        return this;
    }

    public void initPhial() {
        Phial.init(PhialCore.init(this));
    }

    public Application getApplication() {
        return application;
    }

    public List<Shareable> getShareables() {
        return Collections.unmodifiableList(shareables);
    }

    public List<Attacher> getAttachers() {
        return Collections.unmodifiableList(attachers);
    }

    public boolean isAttachScreenShots() {
        return attachScreenShots;
    }

    public boolean isApplySystemInfo() {
        return applySystemInfo;
    }
}
