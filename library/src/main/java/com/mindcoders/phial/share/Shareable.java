package com.mindcoders.phial.share;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public interface Shareable {
    void share(String message, List<File> attachment);

    ShareDescription getDescription();
}
