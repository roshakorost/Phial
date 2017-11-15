package com.mindcoders.phial;

import android.app.Application;

import com.mindcoders.phial.internal.keyvalue.BuildInfoWriter;
import com.mindcoders.phial.internal.keyvalue.InfoWriter;
import com.mindcoders.phial.internal.keyvalue.SystemInfoWriter;
import com.mindcoders.phial.internal.share.attachment.AttacherAdaptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Creates and configures phial overlay pages
 */
public class PhialBuilder {
    private final Application application;
    private final List<Shareable> shareables = new ArrayList<>();
    private final List<ListAttacher> attachers = new ArrayList<>();
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

    /**
     * Adds custom shareables that will be shown under ShareTab.
     * See {@link Shareable}
     *
     * @param shareable shareable that will be  added to ShareTab
     * @return same instance of builder
     */
    public PhialBuilder addShareable(Shareable shareable) {
        this.shareables.add(shareable);
        return this;
    }

    /**
     * Adds custom attacher that will include extra information to share attachment.
     * See {@link Attacher}, {@link ListAttacher}, {@link SimpleFileAttacher} for more infor
     *
     * @param attacher that will include extra information to Share data
     * @return same instance of builder
     */
    public PhialBuilder addAttachmentProvider(Attacher attacher) {
        this.attachers.add(AttacherAdaptor.adapt(attacher));
        return this;
    }

    /**
     * Adds custom attacher that will include extra information to share attachment.
     * See {@link Attacher}, {@link ListAttacher}, {@link SimpleFileAttacher} for more infor
     *
     * @param attacher that will include extra information to Share data
     * @return same instance of builder
     */
    public PhialBuilder addAttachmentProvider(ListAttacher attacher) {
        this.attachers.add(attacher);
        return this;
    }

    /**
     * Rather include screen shot in share attachment.
     *
     * @param attachScreenshot true to include, false to not include.
     *                         default true
     * @return same instance of builder
     */
    public PhialBuilder attachScreenshot(boolean attachScreenshot) {
        this.attachScreenshots = attachScreenshot;
        return this;
    }

    /**
     * Rather include system info (e.g. Android version, screen size ...) in key-value tab and share attachment.
     *
     * @param applySystemInfo true to include, false to not include.
     *                        default true
     * @return same instance of builder
     */
    public PhialBuilder applySystemInfo(boolean applySystemInfo) {
        if (applySystemInfo) {
            this.systemInfoWriter = new SystemInfoWriter(application);
        } else {
            systemInfoWriter = null;
        }
        return this;
    }

    /**
     * Rather include build info (e.g. app version, package ...) in key-value tab and share attachment.
     *
     * @param applyBuildInfo true to include, false to not include.
     *                       default true
     * @return same instance of builder
     */
    public PhialBuilder applyBuildInfo(boolean applyBuildInfo) {
        if (applyBuildInfo) {
            this.buildInfoWriter = new BuildInfoWriter(application);
        } else {
            buildInfoWriter = null;
        }
        return this;
    }

    /**
     * Includes build info (e.g. app version, package ...) in key-value tab and share attachment.
     * And extends it with buildTime and commit.
     * <p>
     * <pre>
     * {@code
     *   def getGitHash = { ->
     *       def stdout = new ByteArrayOutputStream()
     *       exec {
     *           commandLine 'git', 'rev-parse', '--short', 'HEAD'
     *           standardOutput = stdout
     *       }
     *       return stdout.toString().trim()
     *   }
     *
     *   android {
     *       defaultConfig { // or added to target flavor or buildType
     *           buildConfigField "long", "BUILD_TIMESTAMP", "${new Date().time}L"
     *           buildConfigField "String", "GIT_HASH", "\"${getGitHash()}\""
     *       }
     *   }
     * }
     *
     * and call applyBuildInfo(BuildConfig.BUILD_TIMESTAMP, BuildConfig.GIT_HASH)
     * </pre>
     *
     * @param buildTime timestamp when build was build
     * @param commit    commit hash that was used for build
     * @return same instance of builder
     */
    public PhialBuilder applyBuildInfo(long buildTime, String commit) {
        this.buildInfoWriter = new BuildInfoWriter(application, buildTime, commit);
        return this;
    }

    /**
     * Adds custom page to Phial pages.
     * by default Phial has only 2 pages: share and key-value
     *
     * @param page page to add. See {@link Page}
     * @return same instance of builder
     */
    public PhialBuilder addPage(Page page) {
        this.pages.add(page);
        return this;
    }

    public PhialBuilder addPages(Page... pages) {
        return this.addPages(Arrays.asList(pages));
    }

    public PhialBuilder addPages(List<Page> pages) {
        this.pages.addAll(pages);
        return this;
    }

    /**
     * Rather display key value view.
     * The page that show system and build info.
     * <p>
     * See {@link com.mindcoders.phial.keyvalue.Phial} in order to add custom key-values to page
     *
     * @param enableKeyValueView true to display, false to not display.
     *                           default true
     * @return same instance of builder
     */
    public PhialBuilder enableKeyValueView(boolean enableKeyValueView) {
        this.enableKeyValueView = enableKeyValueView;
        return this;
    }

    /**
     * Rather display share view.
     * The page that allows share debug data.
     * <p>
     * The share page can be extended by Shareables. See {@link Shareable}, {@link #addShareable(Shareable)}
     *
     * @param enableShareView true to display, false to not display.
     *                        default true
     * @return same instance of builder
     */
    public PhialBuilder enableShareView(boolean enableShareView) {
        this.enableShareView = enableShareView;
        return this;
    }

    /**
     * Rather include key-values shot in share attachment.
     * Key-values are included as json file
     *
     * @param attachKeyValues true to include, false to not include.
     *                        default true
     * @return same instance of builder
     */
    public PhialBuilder attachKeyValues(boolean attachKeyValues) {
        this.attachKeyValues = attachKeyValues;
        return this;
    }

    /**
     * Set's shared zips archive name.
     * These pattern will be passed to {@link java.text.SimpleDateFormat} so need contain valid pattern for SimpleDateFormat
     *
     * @param shareDataFilePattern the pattern describing the file name format
     * @return same instance of builder
     */
    public PhialBuilder setShareDataFilePattern(String shareDataFilePattern) {
        this.shareDataFilePattern = shareDataFilePattern;
        return this;
    }

    /**
     * Initializes Phial with provided configs and creates overlay view with pages.
     * <p>
     * Calling initPhial will destroy previously created  PhialOverlay instances.
     * In order to destroy Phial call {@link PhialOverlay#destroy()}
     */
    public void initPhial() {
        PhialOverlay.init(this);
    }

    public Application getApplication() {
        return application;
    }

    public List<Shareable> getShareables() {
        return Collections.unmodifiableList(shareables);
    }

    public List<ListAttacher> getAttachers() {
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
