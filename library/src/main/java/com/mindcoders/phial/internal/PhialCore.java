package com.mindcoders.phial.internal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.internal.keyvalue.KVAttacher;
import com.mindcoders.phial.internal.keyvalue.KVSaver;
import com.mindcoders.phial.internal.keyvalue.SystemInfoWriter;
import com.mindcoders.phial.internal.overlay.Overlay;
import com.mindcoders.phial.internal.overlay.OverlayPositionStorage;
import com.mindcoders.phial.internal.share.ShareManager;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.share.attachment.ScreenShotAttacher;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phialkv.Phial;

import java.util.ArrayList;
import java.util.List;

import static com.mindcoders.phial.internal.InternalPhialConfig.DEFAULT_SHARE_IMAGE_QUALITY;
import static com.mindcoders.phial.internal.InternalPhialConfig.SYSTEM_INFO_CATEGORY;

/**
 * Created by rost on 10/23/17.
 */

public final class PhialCore {
    public static final String PREFERENCES_FILE_NAME = "phial";
    private static PhialCore instance;

    private final Application application;
    private final KVSaver kvSaver;
    private final PhialNotifier notifier;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;
    private final CurrentActivityProvider activityProvider;
    private final Overlay overlay;

    private PhialCore(Application application,
                      KVSaver kvSaver,
                      PhialNotifier notifier,
                      ShareManager shareManager,
                      AttachmentManager attachmentManager,
                      CurrentActivityProvider activityProvider, Overlay overlay) {
        this.application = application;
        this.kvSaver = kvSaver;
        this.notifier = notifier;
        this.shareManager = shareManager;
        this.attachmentManager = attachmentManager;
        this.activityProvider = activityProvider;
        this.overlay = overlay;
    }

    public static PhialCore getInstance() {
        return instance;
    }

    public Application getApplication() {
        return application;
    }

    public KVSaver getKvSaver() {
        return kvSaver;
    }

    public PhialNotifier getNotifier() {
        return notifier;
    }

    public ShareManager getShareManager() {
        return shareManager;
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public CurrentActivityProvider getActivityProvider() {
        return activityProvider;
    }

    public static PhialCore init(PhialBuilder phialBuilder) {
        if (instance != null) {
            instance.destroy();
        }

        final Application application = phialBuilder.getApplication();

        final CurrentActivityProvider activityProvider = new CurrentActivityProvider();
        application.registerActivityLifecycleCallbacks(activityProvider);

        final KVSaver kvSaver = new KVSaver();
        Phial.addSaver(kvSaver);

        final PhialNotifier phialNotifier = new PhialNotifier();

        final List<Attacher> attachers = prepareAttachers(phialBuilder, kvSaver, activityProvider);
        final AttachmentManager attachmentManager = createAttachmentManager(phialBuilder, attachers);
        phialNotifier.addListener(attachmentManager);

        final ShareManager shareManager = new ShareManager(application, phialBuilder.getShareables());

        final SharedPreferences prefs = application.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        final OverlayPositionStorage positionStorage = new OverlayPositionStorage(prefs);

        List<Page> pages = new ArrayList<>();
        if (phialBuilder.enableKeyValueView()) {
            pages.add(DefaultPages.KEY_VALUE_PAGE);
        }

        if (phialBuilder.enableShareView()) {
            pages.add(DefaultPages.SHARE_PAGE);
        }

        pages.addAll(phialBuilder.getPages());

        final Overlay overlay = new Overlay(phialNotifier, application, pages, positionStorage);
        activityProvider.addListener(overlay);

        instance = new PhialCore(
                application,
                kvSaver,
                phialNotifier,
                shareManager,
                attachmentManager,
                activityProvider,
                overlay
        );

        if (phialBuilder.applySystemInfo()) {
            SystemInfoWriter.writeSystemInfo(Phial.category(SYSTEM_INFO_CATEGORY), application);
        }

        return instance;
    }

    private void destroy() {
        application.unregisterActivityLifecycleCallbacks(activityProvider);
        overlay.destroy();
    }

    @NonNull
    private static AttachmentManager createAttachmentManager(PhialBuilder phialBuilder, List<Attacher> attachers) {
        return new AttachmentManager(
                attachers,
                InternalPhialConfig.getWorkingDirectory(phialBuilder.getApplication()),
                phialBuilder.getShareDataFilePattern()
        );
    }

    private static List<Attacher> prepareAttachers(
            PhialBuilder phialBuilder,
            KVSaver kvSaver,
            CurrentActivityProvider activityProvider) {
        final List<Attacher> attachers = new ArrayList<>(phialBuilder.getAttachers());

        if (phialBuilder.attachKeyValues()) {
            final KVAttacher attacher = new KVAttacher(
                    kvSaver,
                    InternalPhialConfig.getKeyValueFile(phialBuilder.getApplication())
            );
            attachers.add(attacher);
        }

        if (phialBuilder.attachScreenshots()) {
            final Attacher screenShotAttacher = new ScreenShotAttacher(
                    activityProvider,
                    InternalPhialConfig.getScreenShotFile(phialBuilder.getApplication()),
                    DEFAULT_SHARE_IMAGE_QUALITY
            );
            attachers.add(screenShotAttacher);
        }

        return attachers;
    }
}
