package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23Part2 {
    static Board board;
    static Coord start;
    static Coord end;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day23.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            board = Board.parse(lines);
            System.out.println(board);

            start = new Coord(1, 0);
            end = new Coord(board.width - 2, board.height - 1);

            System.out.println("Part 2: " + longestPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }

    static Map<Coord, Integer> D = new HashMap<>();
    static Set<Coord> visited = new HashSet<>();
    private static int longestPath() {
        for (int row = 0; row < board.height; row++) {
            for (int col = 0; col < board.width; col++) {
                Coord coord = new Coord(col, row);
                D.put(coord, 0);
            }
        }
        getLongestPath(start, 0);

        return D.get(end);
    }

    private static void getLongestPath(Coord coord, int currSum) {
        if (visited.contains(coord)) return;
        visited.add(coord);

        if (D.get(coord) < currSum)
            D.put(coord, currSum);

        if (board.getCell(coord.north()) != Cell.FOREST) getLongestPath(coord.north(), currSum+1);
        if (board.getCell(coord.south()) != Cell.FOREST) getLongestPath(coord.south(), currSum+1);
        if (board.getCell(coord.east()) != Cell.FOREST) getLongestPath(coord.east(), currSum+1);
        if (board.getCell(coord.west()) != Cell.FOREST) getLongestPath(coord.west(), currSum+1);

        visited.remove(coord);
    }
    @RequiredArgsConstructor
    @Data
    static class SearchState {
        final Coord coord;
        final Direction direction;
        final int steps;
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
            if (col < 0 || col >= width) return Cell.FOREST;
            if (row < 0 || row >= height) return Cell.FOREST;
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

        @Override
        public String toString() {
            StringBuilder buff = new StringBuilder();
            for (int row=0; row < height; row++) {
                for (int col=0; col < width; col++) {
                    buff.append(getCell(col, row));
                }
                buff.append("\n");
            }
            return buff.toString();
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;

        public Coord move(Direction direction) {
            switch (direction) {
                case NORTH:
                    return this.north();
                case EAST:
                    return this.east();
                case WEST:
                    return this.west();
                case SOUTH:
                    return this.south();
                default:
                    throw new RuntimeException("Unknown direction");
            }
        }

        public Coord east() {
            return new Coord(x+1, y);
        }
        public Coord north() {
            return new Coord(x, y-1);
        }
        public Coord south() {
            return new Coord(x, y+1);
        }
        public Coord west() {
            return new Coord(x-1, y);
        }
    }

    @RequiredArgsConstructor
    enum Cell {
        PATH('.'),
        FOREST('#'),
        SOUTH_SLOPE('v'),
        NORTH_SLOPE('^'),
        EAST_SLOPE('>'),
        WEST_SLOPE('<');
        final char ch;
        public static Cell parse(char ch) {
            switch (ch) {
                case '#': return FOREST;
                case '.': return PATH;
                case 'v': return SOUTH_SLOPE;
                case '^': return NORTH_SLOPE;
                case '<': return WEST_SLOPE;
                case '>': return EAST_SLOPE;
                default: throw new RuntimeException("Unknown input " + ch);
            }
        }

        @Override
        public String toString() {
            return ""+ch;
        }
    }

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
