package com.mindcoders.phial.sample;

import android.app.Application;
import android.util.Log;

import com.mindcoders.phial.Page;
import com.mindcoders.phial.PhialOverlay;
import com.mindcoders.phial.Shareable;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.jira.JiraShareableBuilder;
import com.mindcoders.phial.logging.PhialLogger;

import java.util.List;

import timber.log.Timber;

import static com.mindcoders.phial.autofill.AutoFiller.createPhialPages;
import static com.mindcoders.phial.autofill.AutoFiller.forActivity;
import static com.mindcoders.phial.autofill.AutoFiller.forScope;
import static com.mindcoders.phial.autofill.AutoFiller.leaveEmpty;
import static com.mindcoders.phial.autofill.AutoFiller.option;

final class ApplicationHook {
    static void onApplicationCreate(Application app) {
        // logs integration example
        final PhialLogger phialLogger = new PhialLogger(app);
        // logs written by timber will be saved in HTML file and attached to DebugInfo
        // Adapter to timber tree interface.
        // If you use different logging facade you should use own adapter that will call phialLogger.log
        Timber.plant(new PhialTimberTree(phialLogger));

        // Jira integration example
        // It will add extra Jira to share window.
        // Sharing to Jira will create new issue with DebugInfo as attachment
        final Shareable jiraShareable = new JiraShareableBuilder(app)
                .setBaseUrl("https://roshakorst.atlassian.net/")
                .setProjectKey("TES")
                //Optional extra fields to be included in Jira issue
                .setFixVersions("testversion")
                .setAffectsVersions("testversion")
                //.setCustomField(key, object) in order to add extra fields to created item
                .build();

        final List<Page> autoFillPages = createPhialPages(
                forActivity(AutoFillActivity.class)
                        .fill(R.id.login, R.id.password)
                        .withOptions(
                                option("user Q", "QQQQQQ", "Qpwdpwd1"),
                                option("user W", "WWWWWW", leaveEmpty()),
                                option("user E", "EEEEEE", "Epwdpwd3"),
                                option("user R", "RRRRRR", "Rpwdpwd4")
                        ),
                forScope("Login")
                        .fill(R.id.login, R.id.password)
                        .withOptions(
                                option("user R", "RRRRRR", "Rpwdpwd4"),
                                option("user T", "TTTTTT", "Tpwdpwd1"),
                                option("user Y", "YYYYYY", leaveEmpty()),
                                option("user U", "UUUUUU", "Upwdpwd3"),
                                option("user I", "IIIIII", "Ipwdpwd4")
                        )
        );

        PhialOverlay.builder(app)
                // By default Phial includes key-values and screenshots as attachment to share
                // When user selects share Phial requests all AttachmentProviders to prepare debug data
                // which will be zipped and shared to selected target.
                // Here we add provider that will include log file.
                .addAttachmentProvider(phialLogger)

                // adding custom attaches that will include shared preferences from device.
                // see SharedPreferencesAttacher about how implement custom attacher.

                // you can also use SimpleFileAttacher(File), SimpleFileAttacher(List<File>) in order to
                // include some files in share attachment.
                .addAttachmentProvider(new SharedPreferencesAttacher(app))

                // adds build time stamp and git hash to build info section.
                // see sample build.gradle how to create these variables.
                .applyBuildInfo(BuildConfig.BUILD_TIMESTAMP, BuildConfig.GIT_HASH)
                // By default in Phials Share tab shows only installed apps that can handle
                // share with zip file attachment.
                // The list of options might be extended by providing custom Shareables.
                // Here we add extra JiraShareable that will add create Jira Issue option in Share Tab
                .addShareable(jiraShareable)
                .addPages(autoFillPages)
                .initPhial();

        //In case you would like to see Phial errors in logcat.
        PhialErrorPlugins.setHandler(new PhialErrorPlugins.ErrorHandler() {
            @Override
            public void onError(Throwable throwable) {
                Log.w("Phial", "error in phial", throwable);
            }
        });
    }

    private static class PhialTimberTree extends Timber.DebugTree {
        private final PhialLogger logger;

        PhialTimberTree(PhialLogger logger) {
            this.logger = logger;
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            // use if (priority > Log.DEBUG) in order to filter some priority
            logger.log(priority, tag, message, t);
        }
    }
}
