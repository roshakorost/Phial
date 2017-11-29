package com.mindcoders.phial.internal;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.ListAttacher;
import com.mindcoders.phial.OverlayCallback;
import com.mindcoders.phial.Page;
import com.mindcoders.phial.PhialBuilder;
import com.mindcoders.phial.R;
import com.mindcoders.phial.TargetScreen;
import com.mindcoders.phial.internal.keyvalue.InfoWriter;
import com.mindcoders.phial.internal.keyvalue.KVAttacher;
import com.mindcoders.phial.internal.keyvalue.KVSaver;
import com.mindcoders.phial.internal.keyvalue.KeyValueView;
import com.mindcoders.phial.internal.share.ShareManager;
import com.mindcoders.phial.internal.share.ShareView;
import com.mindcoders.phial.internal.share.attachment.AttacherAdaptor;
import com.mindcoders.phial.internal.share.attachment.AttachmentManager;
import com.mindcoders.phial.internal.share.attachment.ScreenShotAttacher;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phial.keyvalue.Phial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mindcoders.phial.internal.InternalPhialConfig.DEFAULT_SHARE_IMAGE_QUALITY;

/**
 * Created by rost on 10/23/17.
 */

public final class PhialCore {
    public static final String KEYVALUE_PAGE_KEY = "keyvalue";
    public static final String SHARE_PAGE_KEY = "share";
    private final Application application;
    private final ShareManager shareManager;
    private final AttachmentManager attachmentManager;
    private final KVSaver kvSaver;
    private final PhialNotifier notifier;
    private final CurrentActivityProvider activityProvider;
    private final ScreenTracker screenTracker;
    private final SharedPreferences sharedPreferences;
    private final List<Page> pages;

    private PhialCore(
            Application application,
            ShareManager shareManager,
            AttachmentManager attachmentManager,
            KVSaver kvSaver,
            PhialNotifier notifier,
            CurrentActivityProvider activityProvider,
            ScreenTracker screenTracker,
            SharedPreferences sharedPreferences,
            List<Page> pages) {
        this.application = application;
        this.shareManager = shareManager;
        this.attachmentManager = attachmentManager;
        this.kvSaver = kvSaver;
        this.notifier = notifier;
        this.activityProvider = activityProvider;
        this.screenTracker = screenTracker;
        this.sharedPreferences = sharedPreferences;
        this.pages = pages;
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

        final List<Page> pages = new ArrayList<>();
        if (phialBuilder.enableKeyValueView()) {
            final Page page = createKVPage(application, kvSaver);
            pages.add(page);
        }

        if (phialBuilder.enableShareView()) {
            final Page page = createShareView(application, shareManager, attachmentManager);
            pages.add(page);
        }

        pages.addAll(phialBuilder.getPages());

        return new PhialCore(
                application,
                shareManager,
                attachmentManager,
                kvSaver,
                phialNotifier,
                activityProvider,
                screenTracker,
                sharedPreferences,
                pages
        );
    }

    public void destroy() {
        application.unregisterActivityLifecycleCallbacks(activityProvider);
        Phial.removeSaver(kvSaver);
        notifier.destroy();
        screenTracker.destroy();
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

    public PhialNotifier getNotifier() {
        return notifier;
    }

    public CurrentActivityProvider getActivityProvider() {
        return activityProvider;
    }

    public ScreenTracker getScreenTracker() {
        return screenTracker;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public ShareManager getShareManager() {
        return shareManager;
    }

    public AttachmentManager getAttachmentManager() {
        return attachmentManager;
    }

    public KVSaver getKvSaver() {
        return kvSaver;
    }

    public List<Page> getPages() {
        return pages;
    }

    @NonNull
    private static Page createShareView(Application application, ShareManager shareManager, AttachmentManager attachmentManager) {
        return new Page(
                SHARE_PAGE_KEY,
                R.drawable.ic_share,
                application.getString(R.string.share_page_title),
                new ShareViewFactory(shareManager, attachmentManager),
                Collections.<TargetScreen>emptySet()
        );
    }

    @NonNull
    private static Page createKVPage(Application application, KVSaver kvSaver) {
        return new Page(
                KEYVALUE_PAGE_KEY,
                R.drawable.ic_keyvalue,
                application.getString(R.string.system_info_page_title),
                new KVPageFactory(kvSaver),
                Collections.<TargetScreen>emptySet()
        );
    }

    private static final class KVPageFactory implements Page.PageViewFactory<KeyValueView> {
        private final KVSaver kvSaver;

        KVPageFactory(KVSaver kvSaver) {
            this.kvSaver = kvSaver;
        }

        @Override
        public KeyValueView createPageView(Context context, OverlayCallback overlayCallback) {
            return new KeyValueView(context, kvSaver);
        }
    }

    private static final class ShareViewFactory implements Page.PageViewFactory<ShareView> {
        private final ShareManager shareManager;
        private final AttachmentManager attachmentManager;

        ShareViewFactory(ShareManager shareManager, AttachmentManager attachmentManager) {
            this.shareManager = shareManager;
            this.attachmentManager = attachmentManager;
        }

        @Override
        public ShareView createPageView(Context context, OverlayCallback overlayCallback) {
            return new ShareView(context, shareManager, attachmentManager, overlayCallback);
        }
    }
}
