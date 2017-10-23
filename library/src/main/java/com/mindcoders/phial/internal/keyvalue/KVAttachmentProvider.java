package com.mindcoders.phial.internal.keyvalue;

import com.mindcoders.phial.AttachmentProvider;
import com.mindcoders.phial.internal.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */
class KVAttachmentProvider implements AttachmentProvider {
    private final File targetFile;
    private final KVCategoryProvider categoryProvider;
    private final KVJsonSerializer jsonSerializer;

    public KVAttachmentProvider(File targetDirectory, KVCategoryProvider categoryProvider, KVJsonSerializer jsonSerializer) {
        this.targetFile = targetDirectory;
        this.categoryProvider = categoryProvider;
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public File provideAttachment() throws Exception {
        final List<KVCategory> categories = categoryProvider.getCategories();
        final String text = jsonSerializer.serializeToString(categories);
        FileUtil.write(text, targetFile);
        return targetFile;
    }
}
