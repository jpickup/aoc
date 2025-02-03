package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day16-test.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            Board board = Board.parse(lines);
            Set<Beam> energised = Collections.singleton(new Beam(Direction.EAST, new Coord(0, 0)));
            while (!energised.isEmpty()) {
                energised = board.energise(energised);
            }
            System.out.println("Result: " + board.energisedCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class Board {
        public static Board parse(List<String> lines) {
            int width = lines.get(0).length();
            int height = lines.size();
            Cell[][] cells = new Cell[height][width];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    cells[row][col] = Cell.parse(lines.get(row).charAt(col));
                }
            }
            return new Board(cells, width, height);
        }

        public Set<Beam> energise(Set<Beam> beams) {
            Set<Beam> result = new HashSet<>();
            for (Beam beam : beams) {
                result.addAll(energise(beam));
            }
            return result;
        }

        public Set<Beam> energise(Beam beam) {
            if (!inBounds(beam.coord)) return Collections.emptySet();

            if (energisedDirections.containsKey(beam.coord) &&
                    energisedDirections.get(beam.coord).contains(beam.direction)) return Collections.emptySet();

            if (!energisedDirections.containsKey(beam.coord)) {
                energisedDirections.put(beam.coord, new HashSet<>());
            }
            energisedDirections.get(beam.coord).add(beam.direction);

            List<Direction> newDirections;
            switch (getCell(beam.coord)) {
                case NONE:
                    newDirections = Collections.singletonList(beam.direction);
                    break;
                case VERTICAL:
                    if (beam.direction == Direction.EAST || beam.direction == Direction.WEST)
                        newDirections = Arrays.asList(Direction.NORTH, Direction.SOUTH);
                    else
                        newDirections = Collections.singletonList(beam.direction);
                    break;
                case HORIZONTAL:
                    if (beam.direction == Direction.NORTH || beam.direction == Direction.SOUTH)
                        newDirections = Arrays.asList(Direction.EAST, Direction.WEST);
                    else
                        newDirections = Collections.singletonList(beam.direction);
                    break;
                case SLASH:
                    switch (beam.direction) {
                        case NORTH:
                            newDirections = Collections.singletonList(Direction.EAST);
                            break;
                        case SOUTH:
                            newDirections = Collections.singletonList(Direction.WEST);
                            break;
                        case EAST:
                            newDirections = Collections.singletonList(Direction.NORTH);
                            break;
                        case WEST:
                            newDirections = Collections.singletonList(Direction.SOUTH);
                            break;
                        default:
                            throw new RuntimeException("Unknown direction");
                    }
                    break;
                case BACKSLASH:
                    switch (beam.direction) {
                        case NORTH:
                            newDirections = Collections.singletonList(Direction.WEST);
                            break;
                        case SOUTH:
                            newDirections = Collections.singletonList(Direction.EAST);
                            break;
                        case EAST:
                            newDirections = Collections.singletonList(Direction.SOUTH);
                            break;
                        case WEST:
                            newDirections = Collections.singletonList(Direction.NORTH);
                            break;
                        default:
                            throw new RuntimeException("Unknown direction");
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown direction");
            }
            return newDirections.stream()
                    .map(d -> new Beam(d, beam.coord.move(d)))
                    .filter(b -> inBounds(b.coord))
                    .collect(Collectors.toSet());
        }

        final Cell[][] cells;
        final int width;
        final int height;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    if (energisedDirections.containsKey(coord)) {
                        if (energisedDirections.get(coord).size() > 1) {
                            sb.append(energisedDirections.get(coord).size());
                        } else {
                            sb.append(energisedDirections.get(coord)
                                    .stream()
                                    .findFirst()
                                    .map(d -> d.ch)
                                    .orElse('?'));
                        }

                    } else {
                        sb.append(getCell(coord));
                    }
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        public Cell getCell(int col, int row) {
            if (col < 0 || col >= width || row < 0 || row >= height)
                return Cell.NONE;
            return cells[row][col];
        }

        boolean inBounds(Coord coord) {
            return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height;
        }

        public Cell getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }

        private final Map<Coord, Set<Direction>> energisedDirections = new HashMap<>();

        public int energisedCount() {
            return energisedDirections.size();
        }
    }

    @RequiredArgsConstructor
    enum Cell {
        NONE('.'),
        HORIZONTAL('-'),
        VERTICAL('|'),
        SLASH('/'),
        BACKSLASH('\\');
        final char ch;

        public static Cell parse(char c) {
            switch (c) {
                case '.':
                    return NONE;
                case '-':
                    return HORIZONTAL;
                case '|':
                    return VERTICAL;
                case '/':
                    return SLASH;
                case '\\':
                    return BACKSLASH;
                default:
                    throw new RuntimeException("Unknown input");
            }
        }

        @Override
        public String toString() {
            return ch + "";
        }
    }

    @RequiredArgsConstructor
    enum Direction {
        NORTH('^'),
        SOUTH('v'),
        EAST('>'),
        WEST('<');
        final char ch;
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
            return new Coord(x + 1, y);
        }

        public Coord north() {
            return new Coord(x, y - 1);
        }

        public Coord south() {
            return new Coord(x, y + 1);
        }

        public Coord west() {
            return new Coord(x - 1, y);
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Beam {
        final Direction direction;
        final Coord coord;
    }
}
