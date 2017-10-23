package com.mindcoders.phial.internal;

import android.app.Application;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.internal.keyvalue.KVCategoryProvider;
import com.mindcoders.phial.internal.keyvalue.SystemInfoWriter;
import com.mindcoders.phial.internal.overlay.Overlay;
import com.mindcoders.phial.internal.share.ShareManager;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.share.attachment.ScreenShotAttacher;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;

import java.util.ArrayList;
import java.util.List;

import static com.mindcoders.phial.internal.InternalPhialConfig.DEFAULT_SHARE_IMAGE_QUALITY;
import static com.mindcoders.phial.internal.InternalPhialConfig.SYSTEM_INFO_CATEGORY;

/**
 * Created by rost on 10/23/17.
 */

public final class PhialCore {
    private static PhialCore instance;

    private final Application application;
    private final KVCategoryProvider categoryProvider;
    private final PhialNotifier notifier;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;
    private final CurrentActivityProvider activityProvider;
    private final Overlay overlay;

    private PhialCore(Application application,
                      KVCategoryProvider categoryProvider,
                      PhialNotifier notifier,
                      ShareManager shareManager,
                      AttachmentManager attachmentManager,
                      CurrentActivityProvider activityProvider, Overlay overlay) {
        this.application = application;
        this.categoryProvider = categoryProvider;
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

    public KVCategoryProvider getCategoryProvider() {
        return categoryProvider;
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

        final KVCategoryProvider categoryProvider = new KVCategoryProvider();
        final PhialNotifier phialNotifier = new PhialNotifier();

        final List<Attacher> attachers = prepareAttachers(phialBuilder, activityProvider, phialNotifier);
        final AttachmentManager attachmentManager = new AttachmentManager(attachers);

        final ShareManager shareManager = new ShareManager(application, phialBuilder.getShareables());

        final Overlay overlay = new Overlay(phialNotifier, application, phialBuilder.getPages());
        activityProvider.addListener(overlay);

        instance = new PhialCore(
                application,
                categoryProvider,
                phialNotifier,
                shareManager,
                attachmentManager,
                activityProvider,
                overlay
        );

        if (phialBuilder.isApplySystemInfo()) {
            SystemInfoWriter.writeSystemInfo(categoryProvider.category(SYSTEM_INFO_CATEGORY), application);
        }

        return instance;
    }

    private void destroy() {
        application.unregisterActivityLifecycleCallbacks(activityProvider);
        overlay.destroy();
    }

    private static List<Attacher> prepareAttachers(
            PhialBuilder phialBuilder,
            CurrentActivityProvider activityProvider,
            PhialNotifier notifier) {
        final boolean attachScreenShots = phialBuilder.isAttachScreenshots();
        final List<Attacher> attachers = new ArrayList<>(phialBuilder.getAttachers());
        if (attachScreenShots) {
            final ScreenShotAttacher screenShotAttacher = new ScreenShotAttacher(
                    activityProvider,
                    InternalPhialConfig.getScreenShotFile(phialBuilder.getApplication()),
                    DEFAULT_SHARE_IMAGE_QUALITY
            );
            notifier.addListener(screenShotAttacher);
            attachers.add(screenShotAttacher);
        }
        return attachers;
    }
}
