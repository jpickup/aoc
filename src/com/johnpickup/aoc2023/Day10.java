package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day10.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            Board board = Board.parse(lines);

            board.calcDistances();

            System.out.println("Part 1: " + board.part1());
            System.out.println("Part 2: " + board.part2());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class Board {
        final Pipe[][] cells;
        Map<Coord, Integer> distances;
        public static Board parse(List<String> lines) {
            Pipe[][] cells = new Pipe[lines.size()][lines.get(0).length()];
            for(int y = 0; y < lines.size(); y++) {
                for(int x = 0; x < lines.get(0).length(); x++) {
                    cells[y][x] = Pipe.parse(lines.get(y).charAt(x));
                }
            }
            return new Board(cells);
        }

        int width() {
            return cells[0].length;
        }

        int height() {
            return cells.length;
        }

        public Pipe getCell(int x, int y) {
            if (y < 0 || y >= height() || x < 0 || x > width() ) return Pipe.GROUND;
            return cells[y][x];
        }

        public void setCell(int x, int y, Pipe value) {
            if (y < 0 || y >= height() || x < 0 || x > width() ) return;
            cells[y][x] = value;
        }

        public Pipe getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }

        public void setCell(Coord coord, Pipe value) {
            setCell(coord.x, coord.y, value);
        }

        private List<Coord> findNext(Coord coord) {
            // two coords are only connected if they both have a pipe that links to the other
            List<Coord> potentialNext = findPotentialNext(coord);
            return potentialNext.stream().filter(n -> findPotentialNext(n).contains(coord)).collect(Collectors.toList());
        }

        private List<Coord> findPotentialNext(Coord coord) {
            switch (getCell(coord)) {
                case GROUND: return Collections.emptyList();
                case START: return Arrays.asList(coord.north(), coord.northEast(), coord.east(), coord.southEast(), coord.south(), coord.southWest(), coord.west(), coord.northWest());
                case HORIZONTAL: return Arrays.asList(coord.east(), coord.west());
                case VERTICAL: return Arrays.asList(coord.north(), coord.south());
                case NORTH_EAST: return Arrays.asList(coord.north(), coord.east());
                case NORTH_WEST: return Arrays.asList(coord.north(), coord.west());
                case SOUTH_EAST: return Arrays.asList(coord.south(), coord.east());
                case SOUTH_WEST: return Arrays.asList(coord.south(), coord.west());
                default: throw new RuntimeException("Not mapped");
            }
        }

        private Coord findStart() {
            for(int y = 0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    if (getCell(x, y) == Pipe.START) return new Coord(x, y);
                }
            }
            throw new RuntimeException("Can't find start");
        }

        private void calcDistances() {
            distances = new HashMap<>();
            Coord start = findStart();
            distances.put(start, 0);

            List<Coord> unvisitedAdjacent = new ArrayList<>(findNext(start));

            int iteration = 1;
            while (!unvisitedAdjacent.isEmpty()) {
                List<Coord> nextUnvisited = new ArrayList<>();
                for (Coord coord : unvisitedAdjacent) {
                    distances.put(coord, iteration);
                    List<Coord> nextCoords = findNext(coord);
                    nextUnvisited.addAll(nextCoords.stream().filter(c -> !distances.containsKey(c)).collect(Collectors.toList()));
                }
                unvisitedAdjacent = nextUnvisited;
                iteration++;
            }
        }

        public int part1() {
            return distances.values().stream().reduce(0, Math::max);
        }

        public int part2 () {
            // get possible enclosed spaces
            Set<Coord> coords = new HashSet<>();
            // mutate the board to make it easier to reason about
            // remove junk pipe, replace with ground
            for(int y = 0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    if (!distances.containsKey(new Coord(x, y))) {
                        setCell(x, y, Pipe.GROUND);
                        coords.add(new Coord(x, y));
                    }
                }
            }
            // fix-up start so we know if we need to count it
            Coord start = findStart();
            setCell(start, determinePipe(distances.containsKey(start.north()),
                    distances.containsKey(start.south()),
                    distances.containsKey(start.east()),
                    distances.containsKey(start.west())));

            Set<Coord> enclosed = coords.stream().filter(this::isInside).collect(Collectors.toSet());

            // visualise the board
            for (int y=0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    Coord coord = new Coord(x, y);
                    if (enclosed.contains(coord)) {
                        System.out.print('I');
                    } else {
                        if (distances.containsKey(coord))
                            System.out.print(getCell(x,y).symbol);
                        else
                            System.out.print((getCell(x,y).symbol+"").toLowerCase());
                    }
                }
                System.out.println();
            }

            return enclosed.size();
        }

        private Pipe determinePipe(boolean north, boolean south, boolean east, boolean west) {
            if (north && south) return Pipe.VERTICAL;
            if (east && west) return Pipe.HORIZONTAL;
            if (north && east) return Pipe.NORTH_EAST;
            if (north && west) return Pipe.NORTH_WEST;
            if (south && east) return Pipe.SOUTH_EAST;
            if (south && west) return Pipe.SOUTH_WEST;
            throw new RuntimeException("Can't determine pipe");
        }

        private boolean isInside(Coord coord) {
            if (distances.containsKey(coord)) return false;
            if (coord.y <= 0 || coord.y >= height()-1 || coord.x <= 0 || coord.x >= width()-1 ) return false;
            int boundariesRight = 0;
            for (int x = coord.x+1; x < width(); x++) {
                Coord check = new Coord(x, coord.y);
                if (distances.containsKey(check)) {
                    if (isBoundary(getCell(check)))
                        boundariesRight++;
                }
            }
            return boundariesRight%2==1;
        }

        private boolean isBoundary(Pipe cell) {
            // verticals count, but we also need to count half of the corner types (any pair)
            return cell==Pipe.VERTICAL
                    || cell==Pipe.NORTH_EAST
                    || cell==Pipe.NORTH_WEST;
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;

        public Coord north() {
            return new Coord(x, y-1);
        }
        public Coord northEast() {
            return new Coord(x+1, y-1);
        }
        public Coord east() {
            return new Coord(x+1, y);
        }
        public Coord southEast() {
            return new Coord(x+1, y+1);
        }
        public Coord south() {
            return new Coord(x, y+1);
        }
        public Coord southWest() {
            return new Coord(x-1, y+1);
        }
        public Coord west() {
            return new Coord(x-1, y);
        }
        public Coord northWest() {
            return new Coord(x-1, y-1);
        }

        @Override
        public String toString() {
            return "(" + x +
                    "," + y +
                    ')';
        }
    }

    @RequiredArgsConstructor
    enum Pipe {
        VERTICAL('|'),
        HORIZONTAL('-'),
        NORTH_EAST('L'),
        NORTH_WEST('J'),
        SOUTH_EAST('F'),
        SOUTH_WEST('7'),
        GROUND('.'),
        START('S');
        final char symbol;

        public static Pipe parse(char c) {
            switch (c) {
                case '|': return VERTICAL;
                case '-': return HORIZONTAL;
                case 'L': return NORTH_EAST;
                case 'J': return NORTH_WEST;
                case 'F': return SOUTH_EAST;
                case '7': return SOUTH_WEST;
                case '.': return GROUND;
                case 'S': return START;
                default: throw new RuntimeException("Unknown symbol " + c);
            }
        }
    }

}
