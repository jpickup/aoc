package com.johnpickup.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SetsTest {
    final Set<Integer> ONE = Collections.singleton(1);
    final Set<Integer> TWO = Collections.singleton(2);
    final Set<Integer> ONE_TWO = new HashSet<>();
    final Set<Integer> ONE_TWO_THREE = new HashSet<>();
    final Set<Integer> TWO_THREE = new HashSet<>();

    @Before
    public void setup() {
        ONE_TWO.add(1);
        ONE_TWO.add(2);
        ONE_TWO_THREE.add(1);
        ONE_TWO_THREE.add(2);
        ONE_TWO_THREE.add(3);
        TWO_THREE.add(2);
        TWO_THREE.add(3);
    }

    @Test
    public void union() {
        assertThat(Sets.union(Collections.emptySet(), Collections.emptySet()), is(equalTo(Collections.emptySet())));
        assertThat(Sets.union(ONE, ONE), is(equalTo(ONE)));
        assertThat(Sets.union(ONE, TWO), is(equalTo(ONE_TWO)));
        assertThat(Sets.union(ONE, TWO_THREE), is(equalTo(ONE_TWO_THREE)));
        assertThat(Sets.union(ONE_TWO, TWO_THREE), is(equalTo(ONE_TWO_THREE)));
    }

    @Test
    public void intersection() {
        assertThat(Sets.intersection(Collections.emptySet(), Collections.emptySet()), is(equalTo(Collections.emptySet())));
        assertThat(Sets.intersection(ONE, TWO), is(equalTo(Collections.emptySet())));
        assertThat(Sets.intersection(ONE, ONE), is(equalTo(ONE)));
        assertThat(Sets.intersection(ONE_TWO, TWO_THREE), is(equalTo(TWO)));
    }

    @Test
    public void disjoint() {
        assertThat(Sets.disjoint(Collections.emptySet(), Collections.emptySet()), is(equalTo(Collections.emptySet())));
        assertThat(Sets.disjoint(ONE, TWO), is(equalTo(ONE_TWO)));
        assertThat(Sets.disjoint(ONE, ONE), is(equalTo(Collections.emptySet())));
        assertThat(Sets.disjoint(ONE_TWO_THREE, TWO_THREE), is(equalTo(ONE)));
    }

    @Test
    public void subsets() {
        Set<Integer> EMPTY_SET = Collections.emptySet();
        assertThat(Sets.subsets(EMPTY_SET), is(equalTo(Collections.singleton(EMPTY_SET))));
        assertThat(Sets.subsets(ONE), is(equalTo(Sets.union(Collections.singleton(EMPTY_SET), Collections.singleton(ONE)))));
        Set<Set<Integer>> expected =
                Sets.union(
                    Sets.union(Collections.singleton(EMPTY_SET), Collections.singleton(ONE)),
                    Sets.union(Collections.singleton(TWO), Collections.singleton(ONE_TWO)));
        assertThat(Sets.subsets(ONE_TWO), is(equalTo(expected)));
    }
}