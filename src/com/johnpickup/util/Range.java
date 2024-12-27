package com.johnpickup.util;

import lombok.Data;

import java.util.function.Function;

@Data
public class Range<T extends Comparable<T>> {
    private final T lower;
    private final T upper;


    public Range(String s, Function<String, T> parser) {
        String[] parts = s.split("-");
        lower=parser.apply(parts[0]);
        upper=parser.apply(parts[1]);
    }

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
