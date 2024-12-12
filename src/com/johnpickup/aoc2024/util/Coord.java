package com.johnpickup.aoc2024.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Coord implements Comparable<Coord> {
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

    public boolean isAdjacentTo4(Coord other) {
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        return (!this.equals(other)) && ((dx == 1 && dy == 0) || (dx == 0 && dy == 1));
    }

    public boolean isDiagonallyAdjacentTo(Coord other) {
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        return (!this.equals(other)) && (dx == 1 && dy == 1);
    }

    public boolean isAdjacentTo8(Coord other) {
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        return (!this.equals(other)) && (dx <= 1 && dy <= 1);
    }

    @Override
    public String toString() {
        return "(" + x +
                "," + y +
                ')';
    }

    @Override
    public int compareTo(Coord o) {
        if (this.equals(o)) return 0;
        else if (this.y == o.y) return this.x - o.x;
        return this.y - o.y;
    }
}
