package com.mindcoders.phial.internal.share.attachment;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.PhialListener;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rost on 10/22/17.
 */
public class AttachmentManager implements PhialListener {
    private static final String ZIP_EXTENTION = ".zip";
    private final List<Attacher> providers;
    private final File targetDir;
    private final DateFormat nameDateFormat;

    public AttachmentManager(List<Attacher> providers, File targetDir, String namePattern) {
        this.providers = providers;
        this.targetDir = targetDir;
        nameDateFormat = new SimpleDateFormat(namePattern, Locale.US);
    }

    public File createAttachment() throws IOException {
        final List<File> files = prepareAttachments();
        File target = createTargetFile();
        FileUtil.zip(files, target);
        return target;
    }

    private File createTargetFile() {
        String fileName = nameDateFormat.format(new Date()) + ZIP_EXTENTION;
        return new File(targetDir, fileName);
    }

    private List<File> prepareAttachments() {
        final List<File> result = new ArrayList<>(providers.size());

        for (Attacher provider : providers) {
            try {
                result.add(provider.provideAttachment());
            } catch (Exception ex) {
                PhialErrorPlugins.onError(ex);
            }
        }

        return result;
    }


    @Override
    public void onDebugWindowShow() {
        for (Attacher provider : providers) {
            provider.onPreDebugWindowCreated();
        }
    }

    @Override
    public void onDebugWindowHide() {
        for (Attacher provider : providers) {
            provider.onAttachmentNotNeeded();
        }
    }
}
