package com.johnpickup.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Data
public class Coord implements Comparable<Coord> {
    public static final Coord ORIGIN = new Coord(0, 0);
    final int x;
    final int y;

    public Coord(Coord c) {
        this(c.x, c.y);
    }

    public Coord(String s) {
        String[] parts = s.split(",");
        x = Integer.parseInt(parts[0].trim());
        y = Integer.parseInt(parts[1].trim());
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

    public List<Coord> adjacent4() {
        return Arrays.asList(north(), east(), south(), west());
    }

    public List<Coord> adjacent8() {
        return Arrays.asList(north(), northEast(), east(), southEast(), south(), southWest(), west(), northWest());
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
    public int distanceFrom(Coord other) {
        int dX = Math.abs(x - other.x);
        int dY = Math.abs(y - other.y);
        return dX + dY;
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

    public Coord moveBy(int dx, int dy) {
        return new Coord(x + dx, y + dy);
    }

    /**
     * Rotate by the given number of right angles around the centre point
     * Right angles is +ve for clockwise, -ve for anticlockwise
     * Assumes regular cartesian plane, i.e. E is +ve X, N is +ve Y (Y is opposite to that often used in grids),
     * @param centre    centre around which to rotate
     * @param rightAngles number of right-angle turns, where clockwise is +ve
     * @return new, rotated location
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Coord rotateAround(Coord centre, int rightAngles) {
        Coord offset = new Coord(x - centre.x, y - centre.y);
        while (rightAngles > 0) {
            offset = new Coord(offset.y, -offset.x);
            rightAngles--;
        }
        while (rightAngles < 0) {
            offset = new Coord(-offset.y, offset.x);
            rightAngles++;
        }
        return new Coord(centre.x + offset.x, centre.y + offset.y);
    }

    public Coord move(Direction direction, int distance) {
        Coord result = new Coord(this);
        for (int i = 0; i < distance; i++) {
            result = direction.apply(result);
        }
        return result;
    }

    public Coord calcDelta(Coord coord) {
        return new Coord(coord.x - this.x, coord.y - this.y);
    }

    public Direction directionTo(Coord other) {
        int dx = x - other.x;
        int dy = y - other.y;
        if (dx == 1 && dy == 0) return Direction.WEST;
        if (dx == -1 && dy == 0) return Direction.EAST;
        if (dx == 0 && dy == 1) return Direction.NORTH;
        if (dx == 0 && dy == -1) return Direction.SOUTH;
        throw new RuntimeException("Other coordinate is not adjacent");
    }
}
