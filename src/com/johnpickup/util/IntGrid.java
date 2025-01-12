package com.johnpickup.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public class IntGrid implements Grid<Integer> {
    protected final int width;
    protected final int height;
    protected final int[][] cells;

    public IntGrid(List<String> lines) {
        width = lines.get(0).length();
        height = lines.size();
        cells = new int[lines.get(0).length()][lines.size()];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int value = lines.get(y).charAt(x) - '0';
                cells[x][y] = value;
            }
        }
    }

    public IntGrid(IntGrid source) {
        width = source.getWidth();
        height = source.getHeight();
        cells = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[x][y] = source.cells[x][y];
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int cell = getCell(new Coord(x, y));
                if (cell < 0) sb.appendCodePoint('.');
                if (cell >= 0 && cell < 10) sb.appendCodePoint('0' + cell);
                if (cell >= 10 && cell < 36) sb.appendCodePoint('a' + cell - 10);
                if (cell >= 36 && cell < 62) sb.appendCodePoint('A' + cell - 36);
                if (cell >= 62) sb.appendCodePoint('^');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Integer getCell(Coord coord) {
        if (inBounds(coord)) {
            return cells[coord.x][coord.y];
        } else {
            return -1;
        }
    }

    @Override
    public void setCell(Coord c, Integer value) {
        if (inBounds(c)) {
            cells[c.x][c.y] = value;
        }
    }

    @Override
    public Range<Coord> bounds() {
        return new Range<>(Coord.ORIGIN, new Coord(width-1, height - 1));
    }

    @Override
    public int size() {
        return width * height;
    }

    @Override
    public boolean hasCell(Coord coord) {
        return inBounds(coord);
    }

    @Override
    public Set<Coord> findCells(Integer target) {
        Set<Coord> result = new HashSet<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Coord coord = new Coord(x, y);
                if (getCell(coord).equals(target)) {
                    result.add(coord);
                }
            }
        }
        return result;
    }


    public boolean inBounds(Coord coord) {
        return coord.inBounds(width, height);
    }
    
    /**
     * Create a new grid with the cells reflected horizontally (left-to-right) across a vertical centre line
     */
    public IntGrid flipHorizontal() {
        IntGrid result = new IntGrid(this);
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Coord from = new Coord(x, y);
                Coord to = new Coord(width - x - 1, y);
                result.setCell(to, this.getCell(from));
            }
        }
        return result;
    }

    /**
     * Create a new grid with the cells reflected vertically (top-to-bottom) across a horizontal centre line
     */
    public IntGrid flipVertical() {
        IntGrid result = new IntGrid(this);
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Coord from = new Coord(x, y);
                Coord to = new Coord(x, height - y - 1);
                result.setCell(to, this.getCell(from));
            }
        }
        return result;
    }

    /**
     * Create a new grid with the cells rotated 90 degrees clockwise
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public IntGrid rotateClockwise() {
        IntGrid result = new IntGrid(height, width, new int[height][width]);
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Coord from = new Coord(x, y);
                Coord to = new Coord(height - 1 - y, x);
                result.setCell(to, this.getCell(from));
            }
        }
        return result;
    }

}
