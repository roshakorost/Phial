package com.mindcoders.phial.autofill;

import java.util.List;

public class FillOption {
    private final String name;
    private final List<String> dataToFill;

    FillOption(String name, List<String> dataToFill) {
        this.name = name;
        this.dataToFill = dataToFill;
    }
}
