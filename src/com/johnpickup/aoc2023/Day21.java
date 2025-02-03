package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 {
    static final int MAX_STEPS = 65;
    static Garden garden;
    static Map<Integer, List<SearchState>> stateByCost = new HashMap<>();
    static Map<SearchState, Integer> visited = new HashMap<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day21.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            garden = Garden.parse(lines);
            System.out.println(garden);
            Coord start = garden.findStart();
            System.out.println("Start: " + start);

            int part1 = countDiscoverableCells(start);

            System.out.println(garden);
            System.out.println("Part 1: " + part1);  // 3737
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }

    private static int countDiscoverableCells(Coord start) {
        addState(new SearchState(start, 0), 0);
        while (!stateByCost.isEmpty()) {
            int currentCost = stateByCost.keySet().stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("Failed to find current cost"));

            // prune older visited - not interesting
            visited = visited.entrySet().stream().filter(e -> e.getValue() == currentCost).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            List<SearchState> nextStates = stateByCost.remove(currentCost);
            System.out.println(currentCost + " has " + nextStates.size());
            for (SearchState nextState : nextStates) {
                addState(new SearchState(nextState.coord.north(), nextState.steps+1), currentCost);
                addState(new SearchState(nextState.coord.south(), nextState.steps+1), currentCost);
                addState(new SearchState(nextState.coord.east(), nextState.steps+1), currentCost);
                addState(new SearchState(nextState.coord.west(), nextState.steps+1), currentCost);
            }
        }
        System.out.println(visited.size());

        return visited.entrySet().stream().filter(e -> e.getValue() == MAX_STEPS).map(e -> e.getKey().coord).collect(Collectors.toSet()).size();
    }

    private static boolean addState(SearchState state, int cost) {
        int newCost = cost + 1;
        if (garden.inBounds(state.coord) && garden.getCell(state.coord) != Cell.ROCK && newCost <= MAX_STEPS) {
            if (!visited.containsKey(state)) {
                visited.put(state, newCost);
                if (!stateByCost.containsKey(newCost)) stateByCost.put(newCost, new ArrayList<>());
                stateByCost.get(newCost).add(state);
                return true;
            }
        }
        return false;
    }

    @RequiredArgsConstructor
    @Data
    static class SearchState {
        final Coord coord;
        final int steps;
    }

    @RequiredArgsConstructor
    @Data
    static class Garden {
        private final Cell[][] cells;
        private final int width;
        private final int height;

        public static Garden parse(List<String> lines) {
            int height = lines.size();
            int width = lines.get(0).length();
            Cell[][] cells = new Cell[height][width];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    cells[row][col] = Cell.parse(lines.get(row).charAt(col));
                }
            }
            return new Garden(cells, width, height);
        }

        public Coord findStart() {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    if (getCell(coord).equals(Cell.START)) return coord;
                }
            }
            throw new RuntimeException("Can't find start");
        }

        @Override
        public String toString() {
            Set<Coord> v = visited.entrySet().stream().filter(e -> e.getValue() == MAX_STEPS).map(e -> e.getKey().coord).collect(Collectors.toSet());

            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    if (v.contains(coord)) {
                        sb.append("O");
                    } else {
                        sb.append(getCell(coord).ch);
                    }
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        public Cell getCell(int col, int row) {
            if (col < 0 || col >= width || row < 0 || row >= height)
                return null;
            return cells[row][col];
        }

        public Cell getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }

        boolean inBounds(Coord coord) {
            return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height;
        }
    }

    @RequiredArgsConstructor
    enum Cell {
        START('S'),
        PLOT('.'),
        ROCK('#');

        final char ch;

        public static Cell parse(char c) {
            switch (c) {
                case 'S':
                    return START;
                case '.':
                    return PLOT;
                case '#':
                    return ROCK;
                default:
                    throw new RuntimeException("Unknown cell " + c);
            }
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
    enum Direction {
        NORTH('^'),
        SOUTH('v'),
        EAST('>'),
        WEST('<');

        final char ch;
    }
}
