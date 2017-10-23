package com.mindcoders.phial.internal.keyvalue;

import java.util.ArrayList;
import java.util.List;

class CategoriesConverter {

    List<Item> transform(List<KVCategory> categories) {
        List<Item> items = new ArrayList<>();
        for (KVCategory category : categories) {
            items.add(new Item(category.getName(), Item.TYPE_CATEGORY));
            for (KVEntry entry : category.entries()) {
                items.add(new Item(String.format("%s: %s", entry.getName(), entry.getValue()), Item.TYPE_ENTRY));
            }
        }

        return items;
    }

}
