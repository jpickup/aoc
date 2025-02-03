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

public class Day14Cycles {
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

            int cycleLength=0;

            for (int cycle =0; cycle < cycles; cycle++) {
                if (cycle % 1000000 == 0) {
                    long elapsed = System.currentTimeMillis() - start;
                    System.out.printf("%d in %ds cacheSize=%d%n", cycle, elapsed/1000, cache.size());
                }
                if (cache.containsKey(part2) && cycleLength==0) {
                    System.out.println("In Cycle " + cycle + ", Found matching board at " + cache.get(part2));
                    int cycleStart = cache.get(part2);
                    int cycleFinish = cycle;
                    cycleLength = cycleFinish - cycleStart;
                    System.out.println("Start: " + cycleStart);
                    System.out.println("Length: " + cycleLength);
                    System.out.println("Finish: " + cycleFinish);
                    // now we can skip to the max cycle no before the end
                    cycle = cycleStart + ((cycles - cycleStart) / cycleLength) * cycleLength;
                    System.out.println("New cycle: " + cycle);
                } else {
                    cache.put(part2, cycle);
                }
                part2 = part2.spin();
                System.out.println("load: " + part2.calcLoad());
            }
            System.out.println("Final : " + part2.calcLoad());

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

            Board result = new Board(new Cell[height][width], width, height);
            // init with spaces and cubes
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    result.setCell(col, row, getCell(coord).equals(Cell.CUBE) ? Cell.CUBE : Cell.SPACE);
                }
            }

            boolean dim1IsRow;
            int dim1Start;
            int dim1End;
            int dim1Dir;
            int dim2Start;
            int dim2End;
            int dim2Dir;

            switch (direction) {
                case NORTH:
                    dim1Start = 0;
                    dim1End = height;
                    dim1Dir = 1;
                    dim2Start = 0;
                    dim2End = width;
                    dim2Dir = 1;
                    dim1IsRow = true;
                    break;
                case SOUTH:
                    dim1Start = height - 1;
                    dim1End = -1;
                    dim1Dir = -1;
                    dim2Start = 0;
                    dim2End = width;
                    dim2Dir = 1;
                    dim1IsRow = true;
                    break;
                case EAST:
                    dim1Start = width - 1;
                    dim1End = -1;
                    dim1Dir = -1;
                    dim2Start = 0;
                    dim2End = height;
                    dim2Dir = 1;
                    dim1IsRow = false;
                    break;
                case WEST:
                    dim1Start = 0;
                    dim1End = width;
                    dim1Dir = 1;
                    dim2Start = 0;
                    dim2End = height;
                    dim2Dir = 1;
                    dim1IsRow = false;
                    break;
                default:
                    throw new RuntimeException("Unknown direction");
            }

            for (int dim1 = dim1Start; dim1 != dim1End; dim1 += dim1Dir) {
                for (int dim2 = dim2Start; dim2 != dim2End; dim2 += dim2Dir) {
                    int row = dim1IsRow ? dim1 : dim2;
                    int col = dim1IsRow ? dim2 : dim1;
                    Coord coord = new Coord(col, row);
                    tiltCell(result, coord, direction);
                }
            }
            return result;
        }

        private void tiltCell(Board board, Coord coord, Direction direction) {
            Coord target = coord;
            Coord prevTarget = target;
            if (getCell(coord).equals(Cell.SPHERE)) {
                while (true) {
                    target = target.move(direction);
                    if (!board.getCell(target).equals(Cell.SPACE)) {
                        break;
                    }
                    prevTarget = target;
                }
                board.setCell(prevTarget, Cell.SPHERE);
            }
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

        public Board spin() {
            return
                    tilt(Direction.NORTH)
                    .tilt(Direction.WEST)
                    .tilt(Direction.SOUTH)
                    .tilt(Direction.EAST)
                    ;
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

        public Coord move(Direction direction) {
            switch (direction) {
                case NORTH:
                    return north();
                case SOUTH:
                    return south();
                case EAST:
                    return east();
                case WEST:
                    return west();
                default:
                    throw new RuntimeException("Unknown direction");
            }
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

    @RequiredArgsConstructor
    @Data
    static class Key {
        final Direction direction;
        final Board board;
    }

    static Map<Board, Integer> cache = new HashMap<>();

    enum Direction {
        NORTH,
        SOUTH,
        EAST,
        WEST
    }
}
