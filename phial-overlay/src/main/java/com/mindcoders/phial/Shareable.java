package com.mindcoders.phial;

import java.io.File;
import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

public interface Shareable {
    void share(File attachment, String message);

    ShareDescription getDescription();
}
