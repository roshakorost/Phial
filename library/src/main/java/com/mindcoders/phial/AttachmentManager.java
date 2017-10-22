package com.mindcoders.phial;

import android.support.annotation.WorkerThread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by rost on 10/22/17.
 */
class AttachmentManager {
    interface OnAttachmentsReady {
        @WorkerThread
        void onReady(List<File> attachments);

        @WorkerThread
        void onError(Throwable throwable);
    }

    private final List<AttachmentProvider> providers;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    AttachmentManager(List<AttachmentProvider> providers) {
        this.providers = providers;
    }

    void prepareAttachments(final OnAttachmentsReady callback) {
        final List<Future<File>> futures = new ArrayList<>(providers.size());
        for (AttachmentProvider provider : providers) {
            final Future<File> future = executorService.submit(new PrepareAttachmentCallable(provider));
            futures.add(future);
        }

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                final List<File> result = new ArrayList<>();
                for (Future<File> future : futures) {
                    try {
                        final File attachment = future.get();
                        result.add(attachment);
                    } catch (Exception e) {
                        callback.onError(e);
                    }
                }
                callback.onReady(result);
            }
        });
    }

    private final static class PrepareAttachmentCallable implements Callable<File> {
        private final AttachmentProvider attachmentProvider;

        PrepareAttachmentCallable(AttachmentProvider attachmentProvider) {
            this.attachmentProvider = attachmentProvider;
        }

        @Override
        public File call() throws Exception {
            return attachmentProvider.provideAttachment();
        }
    }
}
