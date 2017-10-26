package com.mindcoders.phial.sample;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rost on 10/26/17.
 */

class ItemRepository {
    private final static List<Item> ITEMS = Arrays.asList(new Item("nameA"), new Item("nameB"), new Item("nameC"));

    public Item loadItem() {
        final int index = new Random().nextInt(ITEMS.size());
        return ITEMS.get(index);
    }

    static class Item {
        private final static AtomicInteger ID_GENERATOR = new AtomicInteger(1);
        private final int id;
        private final String name;

        private Item(String name) {
            this.id = ID_GENERATOR.incrementAndGet();
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "Item(" + id + "){name='" + name + "\'}";
        }
    }
}
