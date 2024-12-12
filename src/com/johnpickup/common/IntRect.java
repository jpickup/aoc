package com.johnpickup.common;

public class IntRect {
    private final Rect<Integer> rect;

    public IntRect(int x1, int y1, int x2, int y2) {
        rect = new Rect<>(x1,y1, x2, y2);
    }

    public IntRect(Coord topLeft, Coord bottomRight) {
        this(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
    }

    public int left() {
        return rect.left();
    }

    public int right() {
        return rect.right();
    }

    public int top() {
        return rect.top();
    }

    public int bottom() {
        return rect.bottom();
    }

    public boolean intersects(IntRect other) {
        return rect.intersects(other.rect);
    }


    public int width() {
        return rect.right() - rect.left();
    }

    public int height() {
        return rect.bottom() - rect.top();
    }

    public int area() {
        return width() * height();
    }

    public Coord topLeft() {
        return new Coord(rect.left(), rect.top());
    }

    public Coord topRight() {
        return new Coord(rect.right(), rect.top());
    }

    public Coord bottomLeft() {
        return new Coord(rect.left(), rect.bottom());
    }

    public Coord bottomRight() {
        return new Coord(rect.right(), rect.bottom());
    }

    public IntRect setWidth(int newWidth) {
        return new IntRect(rect.left(), rect.top(), rect.left() + newWidth, rect.bottom());
    }

    public IntRect setHeight(int newHeight) {
        return new IntRect(rect.left(), rect.top(), rect.left(), rect.top() + newHeight);
    }

    IntRect setTopLeft(Coord tl) {
        return new IntRect(tl.x, tl.y, tl.x + width(), tl.y + height());
    }

    IntRect setTopRight(Coord tl) {
        return new IntRect(tl.x - width(), tl.y, tl.x, tl.y + height());
    }

    IntRect setBottomLeft(Coord tl) {
        return new IntRect(tl.x, tl.y - height(), tl.x + width(), tl.y);
    }

    IntRect setBottomRight(Coord tl) {
        return new IntRect(tl.x - width(), tl.y - height(), tl.x, tl.y);
    }

}
