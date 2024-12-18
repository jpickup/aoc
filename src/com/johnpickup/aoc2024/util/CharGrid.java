package com.johnpickup.aoc2024.util;

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
        for (int y = 0; y < getWidth(); y++) {
            for (int x = 0; x < getHeight(); x++) {
                Coord c = new Coord(x, y);
                if (getCell(c) == find) {
                    result.add(c);
                }
            }
        }
        return result;
    }

}