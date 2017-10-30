package com.mindcoders.phial;

import android.app.Application;

import com.mindcoders.phial.internal.keyvalue.BuildInfoWriter;
import com.mindcoders.phial.internal.keyvalue.InfoWriter;
import com.mindcoders.phial.internal.keyvalue.SystemInfoWriter;
import com.mindcoders.phial.internal.util.Precondition;

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
    private final List<Page> pages = new ArrayList<>();
    private boolean attachScreenshots = true;
    private boolean attachKeyValues = true;
    private InfoWriter systemInfoWriter;
    private InfoWriter buildInfoWriter;
    private boolean enableKeyValueView = true;
    private boolean enableShareView = true;
    private String shareDataFilePattern = "'data_M'MM'D'dd_'H'HH_mm_ss";

    public PhialBuilder(Application application) {
        this.application = application;
        this.systemInfoWriter = new SystemInfoWriter(application);
        this.buildInfoWriter = new BuildInfoWriter(application);
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
        this.attachScreenshots = attachScreenshot;
        return this;
    }

    public PhialBuilder applySystemInfo(boolean applySystemInfo) {
        if (applySystemInfo) {
            this.systemInfoWriter = new SystemInfoWriter(application);
        } else {
            systemInfoWriter = null;
        }
        return this;
    }

    public PhialBuilder applyBuildInfo(boolean applyBuildInfo) {
        if (applyBuildInfo) {
            this.buildInfoWriter = new BuildInfoWriter(application);
        } else {
            buildInfoWriter = null;
        }
        return this;
    }

    public PhialBuilder applyBuildInfo(long buildTime, String commit) {
        this.buildInfoWriter = new BuildInfoWriter(application, buildTime, commit);
        return this;
    }

    public PhialBuilder addPage(Page page) {
        this.pages.add(page);
        return this;
    }

    public PhialBuilder enableKeyValueView(boolean enableKeyValueView) {
        this.enableKeyValueView = enableKeyValueView;
        return this;
    }

    public PhialBuilder enableShareView(boolean enableShareView) {
        this.enableShareView = enableShareView;
        return this;
    }

    public PhialBuilder attachKeyValues(boolean attachKeyValues) {
        this.attachKeyValues = attachKeyValues;
        return this;
    }

    public PhialBuilder setShareDataFilePattern(String shareDataFilePattern) {
        this.shareDataFilePattern = shareDataFilePattern;
        return this;
    }

    public void initPhial() {
        PhialOverlay.init(this);
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

    public boolean attachScreenshots() {
        return attachScreenshots;
    }

    public List<InfoWriter> getInfoWriters() {
        final List<InfoWriter> writers = new ArrayList<>(2);
        if (buildInfoWriter != null) {
            writers.add(buildInfoWriter);
        }
        if (systemInfoWriter != null) {
            writers.add(systemInfoWriter);
        }
        return writers;
    }

    public boolean attachKeyValues() {
        return attachKeyValues;
    }

    public boolean enableKeyValueView() {
        return enableKeyValueView;
    }

    public boolean enableShareView() {
        return enableShareView;
    }

    public String getShareDataFilePattern() {
        return shareDataFilePattern;
    }
}
