package com.johnpickup.util;

import org.junit.Test;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class RangeTest {
    @Test
    public void testParseConstructor() {
        Range<Integer> r = new Range<Integer>("1-2", Integer::parseInt);
        assertThat(r.getLower(), is(1));
        assertThat(r.getUpper(), is(2));
    }

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

    @Test
    public void combineFirstContainsSecond() {
        Range<Integer> r1 = new Range<>(1,6);
        Range<Integer> r2 = new Range<>(3,4);
        Collection<Range<Integer>> combined = Range.combine(r1, r2);
        assertThat(combined.size(), is(1));
        Range<Integer> single = combined.stream().findFirst().orElseThrow();
        assertThat(single.getLower(), is(1));
        assertThat(single.getUpper(), is(6));
    }

    @Test
    public void combineSecondContainsFirst() {
        Range<Integer> r1 = new Range<>(3,4);
        Range<Integer> r2 = new Range<>(1,6);
        Collection<Range<Integer>> combined = Range.combine(r1, r2);
        assertThat(combined.size(), is(1));
        Range<Integer> single = combined.stream().findFirst().orElseThrow();
        assertThat(single.getLower(), is(1));
        assertThat(single.getUpper(), is(6));
    }

    @Test
    public void combineOverlap() {
        Range<Integer> r1 = new Range<>(1,4);
        Range<Integer> r2 = new Range<>(3,6);
        Collection<Range<Integer>> combined = Range.combine(r1, r2);
        assertThat(combined.size(), is(1));
        Range<Integer> single = combined.stream().findFirst().orElseThrow();
        assertThat(single.getLower(), is(1));
        assertThat(single.getUpper(), is(6));
    }

    @Test
    public void combineDisjoint() {
        Range<Integer> r1 = new Range<>(1,3);
        Range<Integer> r2 = new Range<>(4,6);
        Collection<Range<Integer>> combined = Range.combine(r1, r2);
        assertThat(combined.size(), is(2));
    }

    @Test
    public void combineAdjacent() {
        Range<Integer> r1 = new Range<>(1,3);
        Range<Integer> r2 = new Range<>(3,6);
        Collection<Range<Integer>> combined = Range.combine(r1, r2);
        assertThat(combined.size(), is(1));
        Range<Integer> single = combined.stream().findFirst().orElseThrow();
        assertThat(single.getLower(), is(1));
        assertThat(single.getUpper(), is(6));
    }
}