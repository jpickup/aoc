package com.johnpickup.aoc2024.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
}