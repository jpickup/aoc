package com.johnpickup.common;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
class Coord {
    final int x;
    final int y;

    public Coord(Coord c) {
        this(c.x, c.y);
    }

    public boolean inBounds(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Coord north() {
        return new Coord(x, y-1);
    }
    public Coord northEast() {
        return new Coord(x+1, y-1);
    }
    public Coord east() {
        return new Coord(x+1, y);
    }
    public Coord southEast() {
        return new Coord(x+1, y+1);
    }
    public Coord south() {
        return new Coord(x, y+1);
    }
    public Coord southWest() {
        return new Coord(x-1, y+1);
    }
    public Coord west() {
        return new Coord(x-1, y);
    }
    public Coord northWest() {
        return new Coord(x-1, y-1);
    }

    @Override
    public String toString() {
        return "(" + x +
                "," + y +
                ')';
    }
}
