package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day13.txt"))) {
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
            long part2 = 0;

            for (Board board : boards) {
                int reflectRow = board.getReflectRow();
                int reflectCol = board.getReflectCol();
                part1 += (long)reflectCol + 100L * reflectRow;
                if (reflectRow > 0) {
                    // find the col with a single difference
                    reflectRow = board.getSingleDiffReflectRow();
                    reflectCol = board.getSingleDiffReflectCol();
                }
                else {
                    // find the row with a single difference
                    reflectCol = board.getSingleDiffReflectCol();
                    reflectRow = board.getSingleDiffReflectRow();
                }
                part2 += (long)reflectCol + 100L * reflectRow;
            }

            System.out.println("Part 1 : " + part1);
            System.out.println("Part 2 : " + part2);


        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;

        public Coord reflectX(int reflectAt) {
            int before = (reflectAt - this.x);
            int newX = reflectAt + before + 1;
            return new Coord(newX, this.y);
        }

        public Coord reflectY(int reflectAt) {
            int before = (reflectAt - this.y);
            int newY = reflectAt + before + 1;
            return new Coord(this.x, newY);
        }

    }
    @RequiredArgsConstructor
    @Data
    static class Board {
        final Cell[][] cells;
        final int width;
        final int height;

        Cell getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }

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
            for (int reflectRow = 0; reflectRow < height-1; reflectRow++) {
                boolean allMatch = true;
                for (int x=0; x < width; x++) {
                    for (int y=0; y < height; y++) {
                        Coord c1 = new Coord(x, y);
                        Coord c2 = c1.reflectY(reflectRow);
                        allMatch &= getCell(c1).reflects(getCell(c2));
                    }
                }
                if (allMatch) return reflectRow+1;
            }
            return 0;
        }

        public int getReflectCol() {
            for (int reflectCol = 0; reflectCol < width-1; reflectCol++) {
                boolean allMatch = true;
                for (int x=0; x < width; x++) {
                    for (int y=0; y < height; y++) {
                        Coord c1 = new Coord(x, y);
                        Coord c2 = c1.reflectX(reflectCol);
                        allMatch &= getCell(c1).reflects(getCell(c2));
                    }
                }
                if (allMatch) return reflectCol+1;
            }
            return 0;
        }

        public int getSingleDiffReflectCol() {
            for (int reflectCol = 0; reflectCol < width-1; reflectCol++) {
                int diffCount = 0;
                for (int x=0; x < width; x++) {
                    for (int y=0; y < height; y++) {
                        Coord c1 = new Coord(x, y);
                        Coord c2 = c1.reflectX(reflectCol);
                        if (!getCell(c1).reflects(getCell(c2))) diffCount++;
                    }
                }
                if (diffCount == 2) return reflectCol+1;  // every diff is doubled as we scan every cell
            }
            return 0;
        }

        public int getSingleDiffReflectRow() {
            for (int reflectRow = 0; reflectRow < height-1; reflectRow++) {
                int diffCount = 0;
                for (int x=0; x < width; x++) {
                    for (int y=0; y < height; y++) {
                        Coord c1 = new Coord(x, y);
                        Coord c2 = c1.reflectY(reflectRow);
                        if (!getCell(c1).reflects(getCell(c2))) diffCount++;
                    }
                }
                if (diffCount == 2) return reflectRow+1;  // every diff is doubled as we scan every cell
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
            return this==UNKNOWN || other==UNKNOWN || this.equals(other);
        }
    }
}
