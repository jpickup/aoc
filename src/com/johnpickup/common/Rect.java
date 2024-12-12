package com.johnpickup.common;

import lombok.Data;

@Data
public class Rect<T extends Comparable<T>> {
    private final Range<T> x;
    private final Range<T> y;

    Rect(T x1, T y1, T x2, T y2) {
        x = new Range<>(x1, x2);
        y = new Range<>(y1, y2);
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

}
