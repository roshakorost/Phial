package com.mindcoders.phial.keyvalue;

class Item {

    static final int TYPE_CATEGORY = 0;
    static final int TYPE_ENTRY = 1;

    final String value;

    final int type;

    Item(String value, int type) {
        this.value = value;
        this.type = type;
    }

}
