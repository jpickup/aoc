package com.johnpickup.util;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class IntRectTest {

    @Test
    public void width() {
        IntRect lr = new IntRect(1, 2, 3, 4);
        assertThat(lr.width(), is(2L));
        IntRect lr2 = new IntRect(5, 4, 3, 2);
        assertThat(lr2.width(), is(2L));
    }

    @Test
    public void height() {
        IntRect lr = new IntRect(1, 2, 3, 4);
        assertThat(lr.height(), is(2L));
        IntRect lr2 = new IntRect(5, 4, 3, 2);
        assertThat(lr2.height(), is(2L));
    }

    @Test
    public void area() {
        IntRect lr = new IntRect(1, 2, 3, 4);
        assertThat(lr.area(), is(4L));
    }

    @Test
    public void topLeft() {
        IntRect lr = new IntRect(1, 2, 3, 4);
        assertThat(lr.topLeft(), is(new Coord(1,2)));
    }

    @Test
    public void topRight() {
        IntRect lr = new IntRect(1, 2, 3, 4);
        assertThat(lr.topRight(), is(new Coord(3,2)));
    }

    @Test
    public void bottomLeft() {
        IntRect lr = new IntRect(1, 2, 3, 4);
        assertThat(lr.bottomLeft(), is(new Coord(1,4)));
    }

    @Test
    public void bottomRight() {
        IntRect lr = new IntRect(1, 2, 3, 4);
        assertThat(lr.bottomRight(), is(new Coord(3,4)));
    }

    @Test
    public void setWidth() {
        IntRect lr = new IntRect(1, 2, 3, 4).setWidth(4);
        assertThat(lr.left(), is(1L));
        assertThat(lr.width(), is(4L));
        assertThat(lr.right(), is(5L));
    }

    @Test
    public void setHeight() {
        IntRect lr = new IntRect(1, 2, 3, 4).setHeight(4);
        assertThat(lr.left(), is(1L));
        assertThat(lr.height(), is(4L));
        assertThat(lr.bottom(), is(6L));
    }

    @Test
    public void setTopLeft() {
    }

    @Test
    public void setTopRight() {
    }

    @Test
    public void setBottomLeft() {
    }

    @Test
    public void setBottomRight() {
    }
}