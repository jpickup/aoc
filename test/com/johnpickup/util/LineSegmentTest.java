package com.johnpickup.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LineSegmentTest {

    @Test
    public void createFromHorizontal() {
        LineSegment lineSegment = LineSegment.createFrom(new Coord(1, 1), new Coord(2, 1));
        assertThat(lineSegment instanceof LineSegment.HorizontalLineSegment, is(true));
    }

    @Test
    public void createFromVertical() {
        LineSegment lineSegment = LineSegment.createFrom(new Coord(1, 1), new Coord(1, 2));
        assertThat(lineSegment instanceof LineSegment.VerticalLineSegment, is(true));
    }

    @Test(expected = RuntimeException.class)
    public void createFromDiagonal() {
        LineSegment lineSegment = LineSegment.createFrom(new Coord(1, 1), new Coord(2, 2));
    }

    @Test(expected = RuntimeException.class)
    public void createFromPoint() {
        LineSegment lineSegment = LineSegment.createFrom(new Coord(1, 1), new Coord(1, 1));
    }

    @Test
    public void crosses() {
        LineSegment h = LineSegment.createFrom(new Coord(1, 1), new Coord(2, 1));
        LineSegment v = LineSegment.createFrom(new Coord(1, 1), new Coord(1, 2));
        LineSegment v2 = LineSegment.createFrom(new Coord(2, 1), new Coord(2, 2));
        assertThat(v.crosses(h), is(true));
        assertThat(v.crosses(v), is(false));
        assertThat(h.crosses(v), is(true));
        assertThat(h.crosses(v2), is(false));
    }

    @Test
    public void Day9_2025() {
        LineSegment ls = LineSegment.createFrom(new Coord(2,3), new Coord(Integer.MAX_VALUE,3));

        LineSegment b1 = LineSegment.createFrom(new Coord(2,3), new Coord(2, 5));
        LineSegment b2 = LineSegment.createFrom(new Coord(7,1), new Coord(7,3));
        LineSegment b3 = LineSegment.createFrom(new Coord(11,1), new Coord(11,7));

        assertThat(ls.crosses(b1), is(false));
        assertThat(ls.crosses(b2), is(false));
        assertThat(ls.crosses(b3), is(true));
    }

    @Test
    public void contains() {
        LineSegment h = LineSegment.createFrom(new Coord(1, 1), new Coord(2, 1));
        LineSegment v = LineSegment.createFrom(new Coord(1, 1), new Coord(1, 2));
        assertThat(h.contains(new Coord(1, 1)), is(true));
        assertThat(h.contains(new Coord(1, 2)), is(false));
        assertThat(h.contains(new Coord(2, 1)), is(true));
    }
}