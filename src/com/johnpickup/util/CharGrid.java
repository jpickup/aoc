package com.johnpickup.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class CharGrid implements Grid<Character> {
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
        width = lines.stream().map(String::length).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No lines"));
        height = lines.size();
        cells = new char[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = x < lines.get(y).length() ? lines.get(y).charAt(x) : ' ';
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

    @Override
    public Character getCell(Coord coord) {
        if (inBounds(coord)) {
            return cells[coord.x][coord.y];
        } else {
            return ' ';
        }
    }

    @Override
    public void setCell(Coord coord, Character ch) {
        if (inBounds(coord)) {
            cells[coord.x][coord.y] = ch;
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

    @Override
    public Set<Coord> findCells(Character find) {
        return allCells().filter(c -> getCell(c) == find).collect(Collectors.toSet());
    }

    @Override
    public Stream<Coord> allCells() {
        return StreamSupport.stream(cellSpliterator(), false);
    }

    private Spliterator<Coord> cellSpliterator() {
        return new Spliterator<>() {
            int x = 0;
            int y = 0;

            @Override
            public boolean tryAdvance(Consumer<? super Coord> action) {
                if (y < height) {
                    if (x < width) {
                        action.accept(new Coord(x, y));
                        x++;
                        return true;
                    } else {
                        y++;
                        x = 0;
                        if (y < height) {
                            action.accept(new Coord(x, y));
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public Spliterator<Coord> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return (long) width * height;
            }

            @Override
            public int characteristics() {
                return DISTINCT | ORDERED | SIZED | NONNULL;
            }
        };
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