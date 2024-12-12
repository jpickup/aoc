package com.johnpickup.aoc2024.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CoordTest {

    @Test
    public void compareTo() {
        assertThat(new Coord(1,1).compareTo(new Coord(1,1)), is(0));
        assertThat(new Coord(0,1).compareTo(new Coord(1,1)) < 0, is(true));
        assertThat(new Coord(1,1).compareTo(new Coord(0,1)) > 0, is(true));
        assertThat(new Coord(1,0).compareTo(new Coord(1,1)) < 0, is(true));
        assertThat(new Coord(1,1).compareTo(new Coord(1,0)) > 0, is(true));
    }
}