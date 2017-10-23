package com.mindcoders.phial;

import com.mindcoders.phial.internal.PhialCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Application;

/**
 * Created by rost on 10/23/17.
 */

public class PhialBuilder {
    private final Application application;
    private final List<Shareable> shareables = new ArrayList<>();
    private final List<Attacher> attachers = new ArrayList<>();
    private final List<Page> pages = new ArrayList<>();
    private boolean attachScreenShots = true;
    private boolean applySystemInfo = true;

    public PhialBuilder(Application application) {
        this.application = application;
    }

    public PhialBuilder addShareable(Shareable shareable) {
        this.shareables.add(shareable);
        return this;
    }

    public PhialBuilder addAttachmentProvider(Attacher attacher) {
        this.attachers.add(attacher);
        return this;
    }

    public PhialBuilder attachScreenshot(boolean attachScreenshot) {
        this.attachScreenShots = attachScreenshot;
        return this;
    }

    public PhialBuilder applySystemInfo(boolean applySystemInfo) {
        this.applySystemInfo = applySystemInfo;
        return this;
    }

    public PhialBuilder addPage(Page page) {
        this.pages.add(page);
        return this;
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

    public List<Page> getPages() {
        return Collections.unmodifiableList(pages);
    }

    public boolean isAttachScreenshots() {
        return attachScreenShots;
    }

    public boolean isApplySystemInfo() {
        return applySystemInfo;
    }

    public void initPhial() {
        Phial.init(PhialCore.init(this));
    }

}
