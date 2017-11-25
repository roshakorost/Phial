package com.mindcoders.phial.internal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.ListAttacher;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.internal.keyvalue.InfoWriter;
import com.mindcoders.phial.internal.keyvalue.KVAttacher;
import com.mindcoders.phial.internal.keyvalue.KVSaver;
import com.mindcoders.phial.internal.share.ShareManager;
import com.mindcoders.phial.internal.share.attachment.AttacherAdaptor;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.share.attachment.ScreenShotAttacher;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phial.keyvalue.Phial;

import java.util.ArrayList;
import java.util.List;

import static com.mindcoders.phial.internal.InternalPhialConfig.DEFAULT_SHARE_IMAGE_QUALITY;

/**
 * Created by rost on 10/23/17.
 */

public final class PhialCore {
    private final Application application;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;
    private final KVSaver kvSaver;
    private final PhialNotifier notifier;
    private final CurrentActivityProvider activityProvider;
    private final ScreenTracker screenTracker;
    private final SharedPreferences sharedPreferences;

    private PhialCore(
            Application application,
            ShareManager shareManager,
            AttachmentManager attachmentManager,
            KVSaver kvSaver,
            PhialNotifier notifier,
            CurrentActivityProvider activityProvider,
            ScreenTracker screenTracker,
            SharedPreferences sharedPreferences) {
        this.application = application;
        this.shareManager = shareManager;
        this.attachmentManager = attachmentManager;
        this.kvSaver = kvSaver;
        this.notifier = notifier;
        this.activityProvider = activityProvider;
        this.screenTracker = screenTracker;
        this.sharedPreferences = sharedPreferences;
    }

    public static PhialCore create(PhialBuilder phialBuilder) {
        final Application application = phialBuilder.getApplication();

        final CurrentActivityProvider activityProvider = new CurrentActivityProvider();
        final ScreenTracker screenTracker = new ScreenTracker();
        final PhialNotifier phialNotifier = new PhialNotifier();
        final KVSaver kvSaver = new KVSaver();
        final ShareManager shareManager = new ShareManager(
                application,
                InternalPhialConfig.getPhialAuthority(application),
                phialBuilder.getShareables()
        );
        final AttachmentManager attachmentManager = createAttachmentManager(phialBuilder, kvSaver, activityProvider);

        Phial.addSaver(kvSaver);
        phialNotifier.addListener(attachmentManager);
        application.registerActivityLifecycleCallbacks(activityProvider);
        application.registerActivityLifecycleCallbacks(screenTracker);
        PhialScopeNotifier.addListener(screenTracker);

        final List<InfoWriter> writers = phialBuilder.getInfoWriters();
        for (InfoWriter writer : writers) {
            writer.writeInfo();
        }
        final SharedPreferences sharedPreferences = application.getSharedPreferences(
                InternalPhialConfig.PREFERENCES_FILE_NAME,
                Context.MODE_PRIVATE
        );

        return new PhialCore(
                application,
                shareManager,
                attachmentManager,
                kvSaver,
                phialNotifier,
                activityProvider,
                screenTracker,
                sharedPreferences);
    }

    public void destroy() {
        application.unregisterActivityLifecycleCallbacks(activityProvider);
        Phial.removeSaver(kvSaver);
        notifier.destroy();
    }

    @NonNull
    private static AttachmentManager createAttachmentManager(
            PhialBuilder phialBuilder,
            KVSaver kvSaver,
            CurrentActivityProvider activityProvider
    ) {
        final List<ListAttacher> attachers = prepareAttachers(phialBuilder, kvSaver, activityProvider);
        return new AttachmentManager(
                attachers,
                InternalPhialConfig.getWorkingDirectory(phialBuilder.getApplication()),
                phialBuilder.getShareDataFilePattern()
        );
    }

    private static List<ListAttacher> prepareAttachers(
            PhialBuilder phialBuilder,
            KVSaver kvSaver,
            CurrentActivityProvider activityProvider
    ) {
        final List<ListAttacher> attachers = new ArrayList<>(phialBuilder.getAttachers());

        if (phialBuilder.attachKeyValues()) {
            final KVAttacher attacher = new KVAttacher(
                    kvSaver,
                    InternalPhialConfig.getKeyValueFile(phialBuilder.getApplication())
            );
            attachers.add(AttacherAdaptor.adapt(attacher));
        }

        if (phialBuilder.attachScreenshots()) {
            final Attacher screenShotAttacher = new ScreenShotAttacher(
                    activityProvider,
                    InternalPhialConfig.getScreenShotFile(phialBuilder.getApplication()),
                    DEFAULT_SHARE_IMAGE_QUALITY
            );
            attachers.add(AttacherAdaptor.adapt(screenShotAttacher));
        }

        return attachers;
    }

    public Application getApplication() {
        return application;
    }

    ShareManager getShareManager() {
        return shareManager;
    }

    AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    KVSaver getKvSaver() {
        return kvSaver;
    }

    PhialNotifier getNotifier() {
        return notifier;
    }

    CurrentActivityProvider getActivityProvider() {
        return activityProvider;
    }

    ScreenTracker getScreenTracker() {
        return screenTracker;
    }

    SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
