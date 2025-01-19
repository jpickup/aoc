package com.johnpickup.util;

import lombok.Data;

@Data
public class Rect<T extends Comparable<T>> {
    private final Range<T> x;
    private final Range<T> y;

    public Rect(T x1, T y1, T x2, T y2) {
        x = new Range<>(x1, x2);
        y = new Range<>(y1, y2);
    }

    public Rect(Range<T> x, Range<T> y) {
        this.x = x;
        this.y = y;
    }

    public T left() {
        return x.getLower();
    }

    public T right() {
        return x.getUpper();
    }

    public T top() {
        return y.getLower();
    }

    public T bottom() {
        return y.getUpper();
    }

    public boolean intersects(Rect<T> other) {
        return x.overlaps(other.x) && y.overlaps(other.y);
    }

    public Rect<T> intersection(Rect<T> other) {
        if (!intersects(other)) return null;
        return new Rect(x.intersection(other.x), y.intersection(other.y));
    }
}
