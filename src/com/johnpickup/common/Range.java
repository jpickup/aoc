package com.johnpickup.common;

import lombok.Data;

@Data
public class Range<T extends Comparable<T>> {
    private final T lower;
    private final T upper;

    public Range(T v1, T v2) {
        if (v1.compareTo(v2) <= 0) {
            lower = v1;
            upper = v2;
        } else {
            lower = v2;
            upper = v1;
        }
    }

    public boolean overlaps(Range<T> other) {
        return containsValue(other.lower) || containsValue(other.upper);
    }

    public boolean containsValue(T value) {
        return value.compareTo(this.lower) >= 0 && value.compareTo(this.upper) <= 0;
    }
}
