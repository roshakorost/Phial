package com.mindcoders.phial.internal.keyvalue;

import java.util.List;

/**
 * Created by rost on 10/22/17.
 */

interface KVCategory {
    String getName();

    List<KVEntry> entries();

    boolean isEmpty();
}
