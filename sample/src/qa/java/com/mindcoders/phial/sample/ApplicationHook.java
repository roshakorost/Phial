package com.mindcoders.phial.sample;

import android.app.Application;
import android.util.Log;

import com.mindcoders.phial.PhialOverlay;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.jira.JiraShareable;
import com.mindcoders.phial.logging.PhialLogger;

import timber.log.Timber;

final class ApplicationHook {
    static void onApplicationCreate(Application app) {
        final PhialLogger logger = new PhialLogger(app);
        Timber.plant(new PhialTimberTree(logger));

        PhialOverlay.builder(app)
                .addAttachmentProvider(logger)
                .addShareable(new JiraShareable(app))
                .initPhial();

        PhialErrorPlugins.setHandler(new PhialErrorPlugins.ErrorHandler() {
            @Override
            public void onError(Throwable throwable) {
                Log.w("Phial", "error in phial", throwable);
            }
        });
    }

    private static class PhialTimberTree extends Timber.DebugTree {
        private final PhialLogger logger;

        public PhialTimberTree(PhialLogger logger) {
            this.logger = logger;
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            logger.log(priority, tag, message, t);
        }
    }
}
