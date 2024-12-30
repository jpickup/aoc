package com.johnpickup.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class CharGrid {
    final int width;
    final int height;
    final char[][] cells;

    public CharGrid(CharGrid source) {
        width = source.getWidth();
        height = source.getHeight();
        cells = new char[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[x][y] = source.cells[x][y];
            }
        }
    }

    public CharGrid(List<String> lines) {
        width = lines.get(0).length();
        height = lines.size();
        cells = new char[lines.get(0).length()][lines.size()];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = lines.get(y).charAt(x);
                cells[x][y] = c;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                sb.appendCodePoint(getCell(new Coord(x, y)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public char getCell(Coord coord) {
        if (inBounds(coord)) {
            return cells[coord.x][coord.y];
        } else {
            return ' ';
        }
    }

    public void setCell(Coord coord, char ch) {
        if (inBounds(coord)) {
            cells[coord.x][coord.y] = ch;
        }
    }

    public boolean inBounds(Coord coord) {
        return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height;
    }

    public Coord findCharAndCleanup(char find, char replace) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Coord c = new Coord(x, y);
                if (getCell(c) == find) {
                    setCell(c, replace);
                    return c;
                }
            }
        }
        throw new RuntimeException(find + " not found");
    }

    public Set<Coord> findAll(char find) {
        Set<Coord> result = new HashSet<>();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Coord c = new Coord(x, y);
                if (getCell(c) == find) {
                    result.add(c);
                }
            }
        }
        return result;
    }

    /**
     * Create a new grid with the cells reflected horizontally (left-to-right) across a vertical centre line
     */
    public CharGrid flipHorizontal() {
        CharGrid result = new CharGrid(this);
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
    public CharGrid flipVertical() {
        CharGrid result = new CharGrid(this);
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
    public CharGrid rotateClockwise() {
        CharGrid result = new CharGrid(height, width, new char[height][width]);
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Coord from = new Coord(x, y);
                Coord to = new Coord(height - 1 - y, x);
                result.setCell(to, this.getCell(from));
            }
        }
        return result;
    }

    public char[] getRow(int row) {
        char[] result = new char[width];
        for (int x = 0; x < width; x++) {
            result[x] = getCell(new Coord(x, row));
        }
        return result;
    }
    public char[] getCol(int col) {
        char[] result = new char[height];
        for (int y = 0; y < height; y++) {
            result[y] = getCell(new Coord(col, y));
        }
        return result;
    }

    public Set<CharGrid> generateVariants() {
        return generateGridVariants(this);
    }
    public static Set<CharGrid> generateGridVariants(CharGrid grid) {
        Set<CharGrid> result = new HashSet<>();
        result.add(grid);
        result.add(grid.flipHorizontal());
        result.add(grid.flipVertical());
        CharGrid rotated = new CharGrid(grid);
        for (int i = 0; i < 3; i++) {
            rotated = rotated.rotateClockwise();
            result.add(rotated);
            result.add(rotated.flipVertical());
            result.add(rotated.flipHorizontal());
        }
        return result;
    }
}