package com.mindcoders.phial.autofill;

import java.util.List;

public class FillOption {
    private final String name;
    private final List<String> dataToFill;

    FillOption(String name, List<String> dataToFill) {
        this.name = name;
        this.dataToFill = dataToFill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FillOption that = (FillOption) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return dataToFill.equals(that.dataToFill);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + dataToFill.hashCode();
        return result;
    }
}
