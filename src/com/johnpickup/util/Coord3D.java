package com.johnpickup.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Coord3D implements Comparable<Coord3D> {
    public static final Coord3D ORIGIN = new Coord3D(0, 0, 0);
    final int x;
    final int y;
    final int z;

    public Coord3D(Coord3D c) {
        this(c.x, c.y, c.z);
    }

    public Coord3D(String s) {
        String[] parts = s.split(",");
        x = Integer.parseInt(parts[0]);
        y = Integer.parseInt(parts[1]);
        z = Integer.parseInt(parts[2]);
    }

    public boolean inBounds(int width, int height, int depth) {
        return x >= 0 && x < width && y >= 0 && y < height && z >= 0 & z < depth;
    }


    /**
     * Is this 3D coord, if viewed as a unit cube, touching the face of on adjacent unit cube?
     */
    public boolean isAdjacentTo8(Coord3D other) {
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        int dz = Math.abs(z - other.z);
        return (!this.equals(other)) &&
                ((dx == 1 && dy == 0 && dz == 0) || (dx == 0 && dy == 1 && dz == 0) || (dx == 0 && dy == 0 && dz == 1));
    }

    public boolean isAdjacentTo26(Coord3D other) {
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        int dz = Math.abs(z - other.z);
        return (!this.equals(other)) && (dx <= 1 && dy <= 1 && dz <= 1);
    }

    /**
     * Manhattan distance from the other 3D coord
     */
    public int distanceFrom(Coord3D other) {
        int dX = Math.abs(x - other.x);
        int dY = Math.abs(y - other.y);
        int dZ = Math.abs(z - other.z);
        return dX + dY + dZ;
    }

    @Override
    public String toString() {
        return "(" + x +
                "," + y +
                "," + z +
                ')';
    }

    @Override
    public int compareTo(Coord3D o) {
        if (this.equals(o)) return 0;
        else if (this.z == o.z && this.y == o.y) return this.x - o.x;
        else if (this.z == o.z) return this.y - o.y;
        return this.z - o.z;
    }

    public Coord3D moveBy(int dx, int dy, int dz) {
        return new Coord3D(x + dx, y + dy, z + dz);
    }

}
