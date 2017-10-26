package com.mindcoders.phial.internal.share.attachment;

import android.graphics.Bitmap;
import android.view.Window;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.internal.PhialErrorPlugins;
import com.mindcoders.phial.internal.util.CurrentActivityProvider;
import com.mindcoders.phial.internal.util.FileUtil;
import com.mindcoders.phial.internal.util.ScreenShotUtil;

import java.io.File;

/**
 * Created by rost on 10/22/17.
 */

public class ScreenShotAttacher implements Attacher {
    private final CurrentActivityProvider activityProvider;
    private final File targetFile;
    private final int quality;

    public ScreenShotAttacher(CurrentActivityProvider activityProvider, File targetFile, int quality) {
        this.activityProvider = activityProvider;
        this.targetFile = targetFile;
        this.quality = quality;
    }

    @Override
    public File provideAttachment() throws Exception {
        return targetFile;
    }

    @Override
    public void onPreDebugWindowCreated() {
        try {
            final Window window = activityProvider.getActivity().getWindow();
            final Bitmap bitmap = ScreenShotUtil.takeScreenShot(window);
            FileUtil.saveBitmap(bitmap, targetFile, quality);
        } catch (Exception ex) {
            PhialErrorPlugins.onError(ex);
        }
    }

    @Override
    public void onAttachmentNotNeeded() {
        targetFile.delete();
    }
}
