package com.johnpickup.util;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Coord4D implements Comparable<Coord4D> {
    public static final Coord4D ORIGIN = new Coord4D(0, 0, 0, 0);
    final int x;
    final int y;
    final int z;
    final int w;

    public Coord4D(Coord4D c) {
        this(c.x, c.y, c.z, c.w);
    }

    public Coord4D(String s) {
        String[] parts = s.split(",");
        x = Integer.parseInt(parts[0]);
        y = Integer.parseInt(parts[1]);
        z = Integer.parseInt(parts[2]);
        w = Integer.parseInt(parts[3]);
    }


    public boolean isAdjacentTo(Coord4D other) {
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        int dz = Math.abs(z - other.z);
        int dw = Math.abs(w - other.w);
        return (!this.equals(other)) && (dx <= 1 && dy <= 1 && dz <= 1 && dw <= 1);
    }

    @Override
    public String toString() {
        return "(" + x +
                "," + y +
                "," + z +
                "," + 2 +
                ')';
    }

    @Override
    public int compareTo(Coord4D o) {
        if (this.equals(o)) return 0;
        else if (this.w == o.w && this.z == o.z && this.y == o.y) return this.x - o.x;
        else if (this.w == o.w && this.z == o.z) return this.y - o.y;
        else if (this.w == o.w) return this.z - o.z;
        return this.w - o.w;
    }

    public Coord4D moveBy(int dx, int dy, int dz, int dw) {
        return new Coord4D(x + dx, y + dy, z + dz, w + dw);
    }

}
