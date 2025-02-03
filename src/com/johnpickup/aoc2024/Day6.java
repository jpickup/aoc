package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2024/Day6/Day6.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Grid grid = new Grid(lines);
            System.out.println("Part1: " + grid.part1());
            System.out.println("Part2: " + Grid.part2(new Grid(lines)));

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @Data
    static class Grid {
        final int width;
        final int height;
        final Cell[][] cells;
        final Guard guard;

        Grid(List<String> lines) {
            width = lines.get(0).length();
            height = lines.size();
            cells = new Cell[width][height];
            guard = new Guard();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Cell cell = Cell.of(lines.get(y).charAt(x));
                    if (cell == Cell.GUARD) {
                        guard.setInitialLocation(x, y);
                        cells[x][y] = Cell.SPACE;
                    }
                    else {
                        cells[x][y] = cell;
                    }
                }
            }
        }

        Grid(Grid grid) {
            this.width = grid.width;
            this.height = grid.height;
            this.guard = new Guard();
            this.guard.setInitialLocation(grid.guard.location.x, grid.guard.location.y);
            this.cells = new Cell[width][height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    this.cells[x][y] = grid.cells[x][y];
                }
            }
        }

        // Solve part 1 --------------------------
        public int part1() {
            return path().size();
        }

        public Set<Coord> path() {
            Set<Coord> result = new HashSet<>();
            while (guard.inBounds(width, height)) {
                result.add(guard.location());
                guard.move(this);
            }
            return result;
        }

        static int part2(Grid original) {
            int result = 0;
            Set<Coord> initialPath = new Grid(original).path();
            for (Coord coord : initialPath) {
                Grid newGrid = new Grid(original);
                if (!newGrid.guard.isAt(coord)) {
                    newGrid.cells[coord.x][coord.y] = Cell.OBSTACLE;
                    if (newGrid.hasCycle())
                        result += 1;
                }
            }
            return result;
        }

        public boolean hasCycle() {
            Set<GuardState> locations = new HashSet<>();

            while (guard.inBounds(width, height)) {
                guard.move(this);
                if (locations.contains(guard.state())) return true;
                locations.add(guard.state());
            }
            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (guard.isAt(new Coord(x,y))) {
                        sb.append(guard.asChar());
                    }
                    else {
                        sb.append(cells[x][y].asChar());
                    }
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        public Cell getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }

        public Cell getCell(int x, int y) {
            if (x >= 0 && x < width && y >= 0 && y < height)
                return cells[x][y];
            else
                return Cell.SPACE;
        }
    }

    @RequiredArgsConstructor
    enum Cell {
        SPACE('.'),
        OBSTACLE('#'),
        GUARD('^');
        final char ch;

        public static Cell of(char c) {
            switch(c) {
                case '.': return SPACE;
                case '#': return OBSTACLE;
                case '^': return GUARD;
                default: throw new RuntimeException("Unknown cell: "+ c);
            }
        }

        public char asChar() {
            return ch;
        }
    }

    static class Guard {
        Coord location;
        Direction direction;
        void setInitialLocation(int x, int y) {
            this.location = new Coord(x, y);
            this.direction = Direction.NORTH;
        }

        public char asChar() {
            return direction.asChar();
        }

        public boolean isAt(Coord location) {
            return this.location.equals(location);
        }

        public boolean inBounds(int width, int height) {
            return location.x >= 0 && location.x < width && location.y >= 0 && location.y < height;
        }

        public void move(Grid grid) {
            Coord nextLocation = null;
            boolean moved = false;
            while (!moved) {
                nextLocation = location.move(direction);
                Cell nextCell = grid.getCell(nextLocation);
                if (nextCell == Cell.OBSTACLE)
                    rotate();
                else
                    moved = true;
            }
            this.location = nextLocation;
        }

        void rotate() {
            switch (direction) {
                case NORTH: direction = Direction.EAST; break;
                case EAST: direction = Direction.SOUTH; break;
                case SOUTH: direction = Direction.WEST; break;
                case WEST: direction = Direction.NORTH; break;
                default: throw new RuntimeException("Invalid dir");
            }
        }

        public Coord location() {
            return location;
        }

        public GuardState state() {
            return new GuardState(location.x, location.y, direction);
        }
    }

    @RequiredArgsConstructor
    enum Direction {
        NORTH('^'),
        SOUTH('v'),
        EAST('>'),
        WEST('<');

        final char ch;
        public char asChar() {
            return ch;
        }
    }

    @Data
    @RequiredArgsConstructor
    static class Coord {
        final int x;
        final int y;
         public Coord move(Direction direction) {
            switch (direction) {
                case NORTH:
                    return new Coord(x, y - 1);
                case SOUTH:
                    return new Coord(x, y + 1);
                case EAST:
                    return new Coord(x + 1, y);
                case WEST:
                    return new Coord(x - 1, y);
                default:
                    throw new RuntimeException("Invalid dir");
            }
        }
    }

    @Data
    @RequiredArgsConstructor
    static class GuardState {
        final int x;
        final int y;
        final Direction direction;
    }
}
