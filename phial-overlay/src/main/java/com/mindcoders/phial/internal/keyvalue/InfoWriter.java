package com.mindcoders.phial.internal.keyvalue;

import android.content.Context;

import com.mindcoders.phial.keyvalue.Category;
import com.mindcoders.phial.keyvalue.Phial;

/**
 * Created by rost on 10/30/17.
 */

public abstract class InfoWriter {
    private final Category category;
    private final Context context;

    public InfoWriter(Context context, String categoryName) {
        this.category = Phial.category(categoryName);
        this.context = context;
    }

    public final void writeInfo() {
        writeInfo(category);
    }

    protected abstract void writeInfo(Category category);

    protected final Context getContext() {
        return context;
    }
}
