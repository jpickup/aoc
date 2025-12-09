package com.johnpickup.util;

import lombok.RequiredArgsConstructor;

public abstract class LineSegment {
    public static LineSegment createFrom(Coord c1, Coord c2) {
        if (c1.getX() == c2.getX())
            return VerticalLineSegment.of(c1.getX(), c1.getY(), c2.getY());
        else if (c1.getY() == c2.getY())
            return HorizontalLineSegment.of(c1.getX(), c2.getX(), c1.getY());
        else
            throw new RuntimeException("Segment ends must share an X or a Y");
    }

    public abstract boolean crosses(LineSegment other);

    public abstract boolean contains(Coord coord);

    @RequiredArgsConstructor
    static class VerticalLineSegment extends LineSegment {
        final int x;
        final Range<Integer> y;

        public static LineSegment of(int x, int y1, int y2) {
            return new VerticalLineSegment(x, new Range<>(y1, y2));
        }

        @Override
        public boolean crosses(LineSegment other) {
            if (other instanceof HorizontalLineSegment) {
                return y.containsValue(((HorizontalLineSegment)other).y);
            } else {
                return false;
            }
        }

        @Override
        public boolean contains(Coord coord) {
            return (x == coord.x) && y.containsValue(coord.y);
        }
    }

    @RequiredArgsConstructor
    static class HorizontalLineSegment extends LineSegment {
        final Range<Integer> x;
        final int y;

        public static LineSegment of(int x1, int x2, int y) {
            return new HorizontalLineSegment(new Range<>(x1, x2), y);
        }

        @Override
        public boolean crosses(LineSegment other) {
            if (other instanceof VerticalLineSegment) {
                return x.containsValue(((VerticalLineSegment)other).x);
            } else {
                return false;
            }
        }

        @Override
        public boolean contains(Coord coord) {
            return (y == coord.y) && x.containsValue(coord.x);
        }
    }
}
