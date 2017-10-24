package com.mindcoders.phial.internal.keyvalue;

import com.mindcoders.phial.Attacher;
import com.mindcoders.phial.internal.keyvalue.KVSaver.KVCategory;
import com.mindcoders.phial.internal.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */
public class KVAttacher implements Attacher {
    private final File targetFile;
    private final KVSaver kvSaver;
    private final KVJsonSerializer jsonSerializer;


    public KVAttacher(KVSaver kvSaver, File targetFile) {
        this(kvSaver, targetFile, new KVJsonSerializer());
    }

    public KVAttacher(KVSaver kvSaver, File targetFile, KVJsonSerializer jsonSerializer) {
        this.targetFile = targetFile;
        this.kvSaver = kvSaver;
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public File provideAttachment() throws Exception {
        final List<KVCategory> categories = kvSaver.getData();
        final String text = jsonSerializer.serializeToString(categories);
        FileUtil.write(text, targetFile);
        return targetFile;
    }

    @Override
    public void onPreDebugWindowCreated() {

    }

    @Override
    public void onAttachmentNotNeeded() {
        targetFile.delete();
    }
}
