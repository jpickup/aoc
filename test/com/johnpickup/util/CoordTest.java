package com.johnpickup.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CoordTest {

    @Test
    public void rotateAroundOrigin() {
        Coord c = new Coord(10,0);
        assertThat(c.rotateAround(Coord.ORIGIN, 0), is(equalTo(new Coord(10, 0))));
        assertThat(c.rotateAround(Coord.ORIGIN, 1), is(equalTo(new Coord(0, -10))));
        assertThat(c.rotateAround(Coord.ORIGIN, 2), is(equalTo(new Coord(-10, 0))));
        assertThat(c.rotateAround(Coord.ORIGIN, 3), is(equalTo(new Coord(0, 10))));
        assertThat(c.rotateAround(Coord.ORIGIN, 4), is(equalTo(new Coord(10, 0))));
        assertThat(c.rotateAround(Coord.ORIGIN, -1), is(equalTo(new Coord(0, 10))));
        assertThat(c.rotateAround(Coord.ORIGIN, -2), is(equalTo(new Coord(-10, 0))));
        assertThat(c.rotateAround(Coord.ORIGIN, -3), is(equalTo(new Coord(0, -10))));
        assertThat(c.rotateAround(Coord.ORIGIN, -4), is(equalTo(new Coord(10, 0))));

        assertThat(new Coord(-7,2).rotateAround(Coord.ORIGIN, 3), is(equalTo(new Coord(-2,-7))));
    }

    @Test
    public void rotateAroundOffset() {
        Coord c = new Coord(20,10);
        Coord offset = new Coord(20, 20);
        assertThat(c.rotateAround(offset, 0), is(equalTo(new Coord(20, 10))));
        assertThat(c.rotateAround(offset, 1), is(equalTo(new Coord(10, 20))));
        assertThat(c.rotateAround(offset, 2), is(equalTo(new Coord(20, 30))));
        assertThat(c.rotateAround(offset, 3), is(equalTo(new Coord(30, 20))));
        assertThat(c.rotateAround(offset, 4), is(equalTo(new Coord(20, 10))));
        assertThat(c.rotateAround(offset, -1), is(equalTo(new Coord(30, 20))));
        assertThat(c.rotateAround(offset, -2), is(equalTo(new Coord(20, 30))));
        assertThat(c.rotateAround(offset, -3), is(equalTo(new Coord(10, 20))));
        assertThat(c.rotateAround(offset, -4), is(equalTo(new Coord(20, 10))));
    }

}