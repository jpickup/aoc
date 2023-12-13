package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day13-test.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());
            List<String> boardLines = new ArrayList<>();
            List<Board> boards = new ArrayList<>();
            for (String line : lines) {
                if (line.isEmpty()) {
                    boards.add(Board.parse(boardLines));
                    boardLines.clear();
                } else {
                    boardLines.add(line);
                }
            }
            if (!boardLines.isEmpty()) {
                boards.add(Board.parse(boardLines));
            }

            long part1 = 0;

            for (Board board : boards) {
                System.out.println(board);
                int reflectRow = board.getReflectRow();
                int reflectCol = board.getReflectCol();
                System.out.println("row:" + reflectRow + " ; col: " + reflectCol);

                part1 += (long)reflectCol + 100L * reflectRow;
            }

            System.out.println("Part 1 : " + part1);


        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class Board {
        final Cell[][] cells;
        final int width;
        final int height;

        Cell getCell(int col, int row) {
            if (col < 0 || col >= width) return Cell.UNKNOWN;
            if (row < 0 || row >= height) return Cell.UNKNOWN;
            return cells[row][col];
        }

        public static Board parse(List<String> rows) {
            Cell[][] cells = new Cell[rows.size()][rows.get(0).length()];
            for (int row = 0; row < rows.size(); row++) {
                for (int col = 0; col < rows.get(row).length(); col++) {
                    cells[row][col] = Cell.parse(rows.get(row).charAt(col));
                }
            }
            return new Board(cells, rows.get(0).length(), rows.size());
        }

        public int getReflectRow() {
            return 0;
        }

        public int getReflectCol() {
            for (int col = 1; col < width; col++) {
                boolean allMatch = true;
                // do all cols to reflect either side?
                System.out.println("Checking " +col);
                for (int i=0; i < width; i++) {
                    int x1 = col - (i-1);
                    int x2 = col + i;
                    System.out.println(x1 + " vs " +x2);

                    /*
                    on 5
                    5 -> 6   (x - col) + 1
                    4 -> 7
                    3 -> 8
                    2 -> 9
                     */

                    for (int row=0; row < height; row++) {
                        allMatch &= getCell(x1, row) == getCell(x2, row);
                    }
                }
                if (allMatch) return col+1;
            }
            return 0;
        }

    }

    enum Cell {
        ASH,
        ROCK,
        UNKNOWN;
        public static Cell parse(char ch) {
            switch (ch) {
                case '#': return ROCK;
                case '.': return ASH;
                default: return UNKNOWN;
            }
        }

        public boolean reflects(Cell other) {
            return other==UNKNOWN || this.equals(other);
        }
    }

}
