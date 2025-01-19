package com.johnpickup.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RangeTest {

    @Test
    public void intersectionOfDisjoint() {
        Range<Integer> r1 = new Range<>(1,3);
        Range<Integer> r2 = new Range<>(4,6);
        assertThat(r1.intersection(r2), is(nullValue()));
        assertThat(r2.intersection(r1), is(nullValue()));
    }
    @Test
    public void intersectionOfOverlap() {
        Range<Integer> r1 = new Range<>(1,4);
        Range<Integer> r2 = new Range<>(3,6);
        assertThat(r1.intersection(r2), is(equalTo(new Range<>(3,4))));
        assertThat(r2.intersection(r1), is(equalTo(new Range<>(3,4))));
    }

    @Test
    public void intersectionOfCompleteOverlap() {
        Range<Integer> r1 = new Range<>(1,6);
        Range<Integer> r2 = new Range<>(3,4);
        assertThat(r1.intersection(r2), is(equalTo(new Range<>(3,4))));
        assertThat(r2.intersection(r1), is(equalTo(new Range<>(3,4))));
    }
}