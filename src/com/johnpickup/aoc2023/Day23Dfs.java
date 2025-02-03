package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Only works for part 1, for part 2 stack overflow. Need something that's not recursive.
 */
public class Day23Dfs {
    static Board board;
    static Coord start;
    static Coord end;

    static int part = 2;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day23.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            board = Board.parse(lines);
            System.out.println(board);

            start = new Coord(1, 0);
            end = new Coord(board.width - 2, board.height - 1);

            List<Coord> path = findLongestPath(start, Collections.emptyList());
            visualise(board, path);
            System.out.println("Part 1:" + path.size());  // 2430
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }

    private static List<Coord> findLongestPath(Coord from, List<Coord> pathSoFar) {
        if (from.equals(end)) {
            return pathSoFar;
        }

        Cell currentCell = board.getCell(from);
        List<Direction> directions = part==1?currentCell.possibleDirections() : Direction.allDirections();
        List<List<Coord>> paths = new ArrayList<>();
        for (Direction direction : directions) {
            Coord possibleNext = from.move(direction);
            if (board.inBounds(possibleNext)
                    && board.getCell(possibleNext) != Cell.FOREST
                    && !pathSoFar.contains(possibleNext)) {
                paths.add(findLongestPath(possibleNext, appendedList(pathSoFar, possibleNext)));
            }
        }

        List<Coord> longestPath = new ArrayList<>();
        for (List<Coord> path : paths) {
            if (path.size() > longestPath.size()) {
                longestPath = path;
            }
        }
        return longestPath;
    }

    private static List<Coord> appendedList(List<Coord> list, Coord add) {
        List<Coord> result = new ArrayList<>(list);
        result.add(add);
        return result;
    }

    private static void visualise(Board board, List<Coord> path) {
        for (int row = 0; row <board.height; row++) {
            for (int col = 0; col < board.width; col++) {
                Coord coord = new Coord(col, row);
                if (path.contains(coord)) {
                    System.out.print('O');
                }
                else {
                    System.out.print(board.getCell(coord));
                }
            }
            System.out.println();
        }
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

        public boolean inBounds(Coord coord) {
            return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height;
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

        public List<Direction> possibleDirections() {
            switch (this) {
                case PATH: return Direction.allDirections();
                case FOREST: return Collections.emptyList();
                case NORTH_SLOPE: return Collections.singletonList(Direction.NORTH);
                case SOUTH_SLOPE: return Collections.singletonList(Direction.SOUTH);
                case EAST_SLOPE: return Collections.singletonList(Direction.EAST);
                case WEST_SLOPE: return Collections.singletonList(Direction.WEST);
                default: throw new RuntimeException("Unknown direction " + this);
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
        WEST;

        public static List<Direction> allDirections() {
            return Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        }
    }
}
