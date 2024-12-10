package com.johnpickup.aoc2024.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class IntGrid {
        final int width;
        final int height;
        final int[][] cells;

    public IntGrid(List<String> lines) {
            width = lines.get(0).length();
            height = lines.size();
            cells = new int[lines.get(0).length()][lines.size()];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int elev = lines.get(y).charAt(x) - '0';
                    cells[x][y] = elev;
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

        int getCell(Coord coord) {
            if (inBounds(coord)) {
                return cells[coord.x][coord.y];
            } else {
                return -1;
            }
        }

        private boolean inBounds(Coord coord) {
            return coord.inBounds(width, height);
        }
}
