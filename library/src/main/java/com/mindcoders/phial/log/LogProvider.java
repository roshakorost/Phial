package com.mindcoders.phial.log;

import java.io.File;

/**
 * Created by rost on 10/22/17.
 */

public interface LogProvider {
    /**
     * Provides phial with log file that will be associated with debug data.
     * If method returns directory all files from it would be attached to debug data.
     */
    File getCurrentLogFile();
}
