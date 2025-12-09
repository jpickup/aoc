package com.johnpickup.util;

import lombok.Data;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Data
public class Rect<T extends Comparable<T>> implements Comparable<Rect<T>> {
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
        return new Rect<>(x.intersection(other.x), y.intersection(other.y));
    }

    public boolean intersectsInclusive(Rect<T> other) {
        return x.overlapsInclusive(other.x)
                && y.overlapsInclusive(other.y);
    }

    public Rect<T> intersectionInclusive(Rect<T> other) {
        if (!intersectsInclusive(other)) return null;
        return new Rect<>(x.intersectionInclusive(other.x), y.intersectionInclusive(other.y));
    }

    @Override
    public int compareTo(Rect<T> o) {
        int xComp = x.compareTo(o.x);
        int yComp = y.compareTo(o.y);
        return xComp == 0 ? yComp : xComp;
    }

    public Set<Rect<T>> removeArea(Rect<T> areaToRemove) {
        Rect<T> intersection = intersection(areaToRemove);
        if (intersection == null) {
            // nothing to take away
            return Collections.singleton(this);
        }

        if (intersection.equals(this)) {
            // completely covered by the area to remove
            return Collections.emptySet();
        }

        // split into 9 rectangles - return the ones that are inside the original but not inside the removal
        // 4 x coordinates & 4 y coordinates
        Set<T> xs = new TreeSet<>();
        xs.add(areaToRemove.left());
        xs.add(areaToRemove.right());
        xs.add(this.left());
        xs.add(this.right());
        Set<T> ys = new TreeSet<>();
        ys.add(areaToRemove.bottom());
        ys.add(areaToRemove.top());
        ys.add(this.bottom());
        ys.add(this.top());

        Set<Rect<T>> possibleAreas = new TreeSet<>();
        T prevX = null;
        for (T x : xs) {
            T prevY = null;
            for (T y : ys) {
                if (prevX != null && prevY != null
                        && x.compareTo(prevX) != 0 && y.compareTo(prevY) != 0) {
                    possibleAreas.add(new Rect<>(prevX, prevY, x, y));
                }
                prevY = y;
            }
            prevX = x;
        }

        return possibleAreas.stream()
                .filter(a -> this.intersects(a))           // inside original
                .filter(a -> !areaToRemove.intersects(a))  // not inside removal
                .collect(Collectors.toSet());
    }
}
