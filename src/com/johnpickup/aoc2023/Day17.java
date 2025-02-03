package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 {
    static City city;
    static Map<Integer, List<SearchState>> stateByCost = new HashMap<>();
    static Map<SearchState, Integer> visited = new HashMap<>();
    static Coord start;
    static Coord end;

    static int part = 2;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day17.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            city = City.parse(lines);
            System.out.println(city);

            start = new Coord(0, 0);
            end = new Coord(city.width - 1, city.height - 1);

            addState(new SearchState(start, Direction.EAST, 1), 0);
            addState(new SearchState(start, Direction.SOUTH, 1), 0);

            while (true) {
                int currentCost = stateByCost.keySet().stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("Failed to find current cost"));
                List<SearchState> nextStates = stateByCost.remove(currentCost);
                for (SearchState nextState : nextStates) {
                    if (part == 1 || nextState.distance >= 4) {
                        addState(new SearchState(nextState.coord.move(nextState.direction.turnLeft()), nextState.direction.turnLeft(), 1), currentCost);
                        addState(new SearchState(nextState.coord.move(nextState.direction.turnRight()), nextState.direction.turnRight(), 1), currentCost);
                    }
                    if ((part == 1 && nextState.distance < 3) || (part == 2 && nextState.distance < 10)) {
                        addState(new SearchState(nextState.coord.move(nextState.direction), nextState.direction, nextState.distance+1), currentCost);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }

    private static void addState(SearchState state, int cost) {
        if (city.inBounds(state.coord)) {
            int newCost = cost + city.getCell(state.coord);
            if (state.coord.equals(end)) {
                System.out.println("Found the end, cost = " + (newCost - city.getCell(start)));
                System.exit(0); // yuck!
            }
            if (!visited.containsKey(state)) {
                visited.put(state, newCost);
                if (!stateByCost.containsKey(newCost)) stateByCost.put(newCost, new ArrayList<>());
                stateByCost.get(newCost).add(state);
            }
        }
    }

    @RequiredArgsConstructor
    @Data
    static class SearchState {
        final Coord coord;
        final Direction direction;
        final int distance;
    }

    @RequiredArgsConstructor
    @Data
    static class City {
        private final int[][] blocks;
        private final int width;
        private final int height;
        public static City parse(List<String> lines) {
            int height = lines.size();
            int width = lines.get(0).length();
            int[][] blocks = new int[height][width];
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    blocks[row][col] = Integer.parseInt(lines.get(row).charAt(col) + "");
                }
            }
            return new City(blocks, width, height);
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    Coord coord = new Coord(col, row);
                    sb.append(getCell(coord));
                }
                sb.append('\n');
            }
            return sb.toString();
        }
        public int getCell(int col, int row) {
            if (col < 0 || col >= width || row < 0 || row >= height)
                return -1;
            return blocks[row][col];
        }
        public int getCell(Coord coord) {
            return getCell(coord.x, coord.y);
        }
        boolean inBounds(Coord coord) {
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

        public Direction turnLeft() {
            switch (this) {
                case NORTH:
                    return WEST;
                case WEST:
                    return SOUTH;
                case SOUTH:
                    return EAST;
                case EAST:
                    return NORTH;
                default:
                    throw new RuntimeException("Unknown direction " + this);
            }
        }

        public Direction turnRight() {
            switch (this) {
                case NORTH:
                    return EAST;
                case EAST:
                    return SOUTH;
                case SOUTH:
                    return WEST;
                case WEST:
                    return NORTH;
                default:
                    throw new RuntimeException("Unknown direction " + this);
            }
        }
        final char ch;
    }
}
