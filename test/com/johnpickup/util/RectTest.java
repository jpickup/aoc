package com.johnpickup.util;

import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RectTest {

    @Test
    public void removesAreaDisjoint() {
        Rect<Integer> r1 = new Rect<>(1, 1, 10, 10);
        Rect<Integer> r2 = new Rect<>(10, 10, 20, 20);
        assertThat(r1.removeArea(r2), is(Collections.singleton(r1)));
        assertThat(r2.removeArea(r1), is(Collections.singleton(r2)));
        assertThat(r1.removeArea(r1), is(Collections.emptySet()));
    }

    @Test
    public void removesAreaEnclosing() {
        Rect<Integer> r1 = new Rect<>(1, 1, 10, 10);
        Rect<Integer> r2 = new Rect<>(3, 5, 6, 8);
        Set<Rect<Integer>> expected = generateAll(r1, r2);
        expected.remove(r2);
        assertThat(r1.removeArea(r2), is(equalTo(expected)));
        assertThat(r2.removeArea(r1), is(Collections.emptySet()));
    }

    @Test
    public void removesAreaOverlapping() {
        Rect<Integer> r1 = new Rect<>(1, 1, 6, 8);
        Rect<Integer> r2 = new Rect<>(3, 5, 10, 10);
        Set<Rect<Integer>> expected1 = new TreeSet<>();
        expected1.add(new Rect<>(1, 1, 3, 5));
        expected1.add(new Rect<>(3, 1, 6, 5));
        expected1.add(new Rect<>(1, 5, 3, 8));
        assertThat(r1.removeArea(r2), is(equalTo(expected1)));

        Set<Rect<Integer>> expected2 = new TreeSet<>();
        expected2.add(new Rect<>(6, 5, 10, 8));
        expected2.add(new Rect<>(6, 8, 10, 10));
        expected2.add(new Rect<>(3, 8, 6, 10));
        assertThat(r2.removeArea(r1), is(equalTo(expected2)));
    }

    private Set<Rect<Integer>> generateAll(Rect<Integer> r1, Rect<Integer> r2) {
        TreeSet<Integer> xs = new TreeSet<>();
        xs.add(r1.left());
        xs.add(r1.right());
        xs.add(r2.left());
        xs.add(r2.right());
        TreeSet<Integer> ys = new TreeSet<>();
        ys.add(r1.bottom());
        ys.add(r1.top());
        ys.add(r2.bottom());
        ys.add(r2.top());
        return generateAll(xs, ys);
    }

    private Set<Rect<Integer>> generateAll(TreeSet<Integer> xs, TreeSet<Integer> ys) {
        Set<Rect<Integer>> result = new TreeSet<>();
        Integer prevX = null;
        for (Integer x : xs) {
            Integer prevY = null;
            for (Integer y : ys) {
                if (prevX != null && prevY != null
                        && x.compareTo(prevX) != 0 && y.compareTo(prevY) != 0) {
                    result.add(new Rect<>(prevX, prevY, x, y));
                }
                prevY = y;
            }
            prevX = x;
        }
        return result;
    }
}