package com.johnpickup.util;

import lombok.RequiredArgsConstructor;

public abstract class LineSegment {
    public static LineSegment createFrom(double x1, double y1, double x2, double y2) {
        if (x1==x2 && y1==y2)
            throw new RuntimeException("Segment requires two different points");
        if (x1 == x2)
            return VerticalLineSegment.of(x1, y1, y2);
        else if (y1 == y2)
            return HorizontalLineSegment.of(x1, x2, y1);
        else
            throw new RuntimeException("Segment ends must share an X or a Y");
    }

    public abstract boolean crosses(LineSegment other);

    public abstract boolean contains(double x, double y);

    @RequiredArgsConstructor
    static class VerticalLineSegment extends LineSegment {
        final double x;
        final Range<Double> y;

        public static LineSegment of(double x, double y1, double y2) {
            return new VerticalLineSegment(x, new Range<>(y1, y2));
        }

        @Override
        public boolean crosses(LineSegment other) {
            if (other instanceof HorizontalLineSegment) {
                HorizontalLineSegment hls = (HorizontalLineSegment) other;
                return y.getLower() <= hls.y && y.getUpper() > hls.y
                        && hls.x.getLower() <= this.x && hls.x.getUpper() > this.x;
            } else {
                return false;
            }
        }

        @Override
        public boolean contains(double x, double y) {
            return (this.x == x) && this.y.getLower() <= y && this.y.getUpper() > y;
        }
    }

    @RequiredArgsConstructor
    static class HorizontalLineSegment extends LineSegment {
        final Range<Double> x;
        final double y;

        public static LineSegment of(double x1, double x2, double y) {
            return new HorizontalLineSegment(new Range<>(x1, x2), y);
        }

        @Override
        public boolean crosses(LineSegment other) {
            if (other instanceof VerticalLineSegment) {
                VerticalLineSegment vls = (VerticalLineSegment) other;
                return x.getLower() <= vls.x && x.getUpper() > vls.x
                        && vls.y.getLower() <= this.y && vls.y.getUpper() > this.y;
            } else {
                return false;
            }
        }

        @Override
        public boolean contains(double x, double y) {
            return (this.y == y) && this.x.getLower() <= x && this.x.getUpper() > x;
        }
    }
}
