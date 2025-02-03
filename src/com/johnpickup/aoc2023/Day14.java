package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day14.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Board board = Board.parse(lines);
            System.out.println(board);

            System.out.println("Part 1 :");
            Board part1 = board.tilt(Direction.NORTH);
            System.out.println(part1.calcLoad());

            System.out.println("Part 2 :");
            int cycles = 1000000000;
            Board part2 = board;

            for (int cycle =0; cycle < cycles; cycle++) {
                if (cycle % 1000 == 0) {
                    long elapsed = System.currentTimeMillis() - start;
                    System.out.printf("%d in %ds %n", cycle, elapsed/1000);
                }
                part2 = part2.spin();
            }
            System.out.println(part2.calcLoad());


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

        Cell getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }

        Cell getCell(int col, int row) {
            if (col < 0 || col >= width) return Cell.CUBE;
            if (row < 0 || row >= height) return Cell.CUBE;
            return cells[row][col];
        }

        void setCell(Coord coord, Cell value) {
            setCell(coord.x, coord.y, value);
        }

        void setCell(int col, int row, Cell value) {
            if (col < 0 || col >= width) return;
            if (row < 0 || row >= height) return;
            cells[row][col] = value;
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

        public Board tilt(Direction direction) {
            switch (direction) {
                case NORTH:
                    return tiltNorth();
                case SOUTH:
                    return tiltSouth();
                case EAST:
                    return tiltEast();
                case WEST:
                    return tiltWest();
                default:
                    throw new RuntimeException("Unknown direction");
            }
        }

        public Board tiltNorth() {
            if (northCache.containsKey(this)) return northCache.get(this);

            Board result = new Board(new Cell[height][width], width, height);
            // init with spaces and cubes
            for (int row=0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    result.setCell(col, row, getCell(coord).equals(Cell.CUBE) ? Cell.CUBE : Cell.SPACE);
                }
            }

            for (int row=0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    Coord target = new Coord(col, row);
                    Coord prevTarget = target;
                    if (getCell(coord).equals(Cell.SPHERE)) {
                        while (true) {
                            target = target.north();
                            if (!result.getCell(target).equals(Cell.SPACE)) {
                                break;
                            }
                            prevTarget = target;
                        }
                        result.setCell(prevTarget, Cell.SPHERE);
                    }
                }
            }
            northCache.put(this, result);
            return result;
        }

        public Board tiltEast() {
            if (eastCache.containsKey(this)) return eastCache.get(this);

            Board result = new Board(new Cell[height][width], width, height);
            // init with spaces and cubes
            for (int row=0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    result.setCell(col, row, getCell(coord).equals(Cell.CUBE) ? Cell.CUBE : Cell.SPACE);
                }
            }

            for (int col = width-1; col >=0; col--) {
                for (int row=0; row < height; row++) {
                    Coord coord = new Coord(col, row);
                    Coord target = new Coord(col, row);
                    Coord prevTarget = target;
                    if (getCell(coord).equals(Cell.SPHERE)) {
                        while (true) {
                            target = target.east();
                            if (!result.getCell(target).equals(Cell.SPACE)) {
                                break;
                            }
                            prevTarget = target;
                        }
                        result.setCell(prevTarget, Cell.SPHERE);
                    }
                }
            }
            eastCache.put(this, result);
            return result;
        }

        public Board tiltSouth() {
            if (southCache.containsKey(this)) return southCache.get(this);

            Board result = new Board(new Cell[height][width], width, height);
            // init with spaces and cubes
            for (int row=0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    result.setCell(col, row, getCell(coord).equals(Cell.CUBE) ? Cell.CUBE : Cell.SPACE);
                }
            }

            for (int row=height-1; row >= 0; row--) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    Coord target = new Coord(col, row);
                    Coord prevTarget = target;
                    if (getCell(coord).equals(Cell.SPHERE)) {
                        while (true) {
                            target = target.south();
                            if (!result.getCell(target).equals(Cell.SPACE)) {
                                break;
                            }
                            prevTarget = target;
                        }
                        result.setCell(prevTarget, Cell.SPHERE);
                    }
                }
            }
            southCache.put(this, result);
            return result;
        }

        public Board tiltWest() {
            if (westCache.containsKey(this)) return westCache.get(this);

            Board result = new Board(new Cell[height][width], width, height);
            // init with spaces and cubes
            for (int row=0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    result.setCell(col, row, getCell(coord).equals(Cell.CUBE) ? Cell.CUBE : Cell.SPACE);
                }
            }

            for (int col = 0; col < width; col++) {
                for (int row=0; row < height; row++) {
                    Coord coord = new Coord(col, row);
                    Coord target = new Coord(col, row);
                    Coord prevTarget = target;
                    if (getCell(coord).equals(Cell.SPHERE)) {
                        while (true) {
                            target = target.west();
                            if (!result.getCell(target).equals(Cell.SPACE)) {
                                break;
                            }
                            prevTarget = target;
                        }
                        result.setCell(prevTarget, Cell.SPHERE);
                    }
                }
            }
            westCache.put(this, result);
            return result;
        }

        public long calcLoad() {
            long result = 0;
            for (int row=0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    if (getCell(col, row).equals(Cell.SPHERE)) {
                        result += rowLoad(row);
                    }
                }
            }
            return result;
        }

        private long rowLoad(int row) {
            return height - row;
        }

        static Map<Board, Board> northCache = new HashMap<>();
        static Map<Board, Board> southCache = new HashMap<>();
        static Map<Board, Board> eastCache = new HashMap<>();
        static Map<Board, Board> westCache = new HashMap<>();

        public Board spin() {
            return tilt(Direction.NORTH)
                    .tilt(Direction.WEST)
                    .tilt(Direction.SOUTH)
                    .tilt(Direction.EAST);
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;

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

    enum Cell {
        SPACE,
        CUBE,
        SPHERE;
        public static Cell parse(char ch) {
            switch (ch) {
                case '#': return CUBE;
                case '.': return SPACE;
                case 'O': return SPHERE;
                default: throw new RuntimeException("Unknown input " + ch);
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case CUBE: return "#";
                case SPHERE: return "O";
                case SPACE: return ".";
                default: throw new RuntimeException("Unmapped cell type");
            }
        }
    }

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
