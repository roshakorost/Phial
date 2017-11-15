package com.mindcoders.phial.autofill;

import java.util.Collections;
import java.util.List;

public class FillOption {
    private final String name;
    private final List<String> dataToFill;
    private final List<Integer> ids;

    FillOption(String name, List<String> dataToFill) {
        this(name, dataToFill, Collections.emptyList());
    }

    FillOption(String name, List<String> dataToFill, List<Integer> ids) {
        this.name = name;
        this.dataToFill = Collections.unmodifiableList(dataToFill);
        this.ids = Collections.unmodifiableList(ids);
    }

    FillOption withIds(List<Integer> ids) {
        return new FillOption(name, dataToFill, ids);
    }

    String getName() {
        return name;
    }

    List<String> getDataToFill() {
        return dataToFill;
    }

    List<Integer> getIds() {
        return ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FillOption that = (FillOption) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!dataToFill.equals(that.dataToFill)) return false;
        return ids.equals(that.ids);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + dataToFill.hashCode();
        result = 31 * result + ids.hashCode();
        return result;
    }
}
