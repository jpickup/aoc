package com.johnpickup.util;

import lombok.Data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@Data
public class Range<T extends Comparable<T>> implements Comparable<Range<T>> {
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
        T maxLower = lower.compareTo(other.lower) > 0 ? lower : other.lower;
        T minUpper = upper.compareTo(other.upper) < 0 ? upper : other.upper;
        return maxLower.compareTo(minUpper) < 0;
    }

    public boolean overlapsInclusive(Range<T> other) {
        T maxLower = lower.compareTo(other.lower) > 0 ? lower : other.lower;
        T minUpper = upper.compareTo(other.upper) < 0 ? upper : other.upper;
        return maxLower.compareTo(minUpper) <= 0;
    }

    public boolean containsValue(T value) {
        return value.compareTo(this.lower) >= 0 && value.compareTo(this.upper) <= 0;
    }

    public Range<T> intersection(Range<T> other) {
        if (!overlaps(other)) return null;

        T maxLower = lower.compareTo(other.lower) > 0 ? lower : other.lower;
        T minUpper = upper.compareTo(other.upper) < 0 ? upper : other.upper;

        return new Range<>(maxLower, minUpper);
    }

    public Range<T> intersectionInclusive(Range<T> other) {
        if (!overlapsInclusive(other)) return null;

        T maxLower = lower.compareTo(other.lower) > 0 ? lower : other.lower;
        T minUpper = upper.compareTo(other.upper) < 0 ? upper : other.upper;

        return new Range<>(maxLower, minUpper);
    }

    public Collection<Range<T>> combineWith(Range<T> other) {
        // ranges are disjoint
        if (!overlapsInclusive(other)) {
            return Arrays.asList(this, other);
        }

        T newLower = lower.compareTo(other.lower) < 0 ? lower : other.lower;
        T newUpper = upper.compareTo(other.upper) > 0 ? upper : other.upper;

        return Collections.singletonList(new Range<>(newLower, newUpper));
    }

    public static <T extends Comparable<T>> Collection<Range<T>> combine(Range<T> r1, Range<T> r2) {
        return r1.combineWith(r2);
    }

    @Override
    public int compareTo(Range<T> o) {
        int compareLower = lower.compareTo(o.lower);
        if (compareLower != 0) return compareLower;
        return upper.compareTo(o.upper);
    }
}
