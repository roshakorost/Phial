package com.mindcoders.phial.sample;

import android.app.Application;

import com.mindcoders.phial.PhialOverlay;
import com.mindcoders.phial.logging.PhialLogger;

import timber.log.Timber;

final class ApplicationHook {
    static void onApplicationCreate(Application app) {
        final PhialLogger logger = new PhialLogger(app);
        Timber.plant(new PhialTimberTree(logger));

        PhialOverlay.builder(app)
                .addAttachmentProvider(logger)
                .initPhial();
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
