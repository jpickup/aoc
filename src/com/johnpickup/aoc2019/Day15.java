package com.johnpickup.aoc2019;

import com.johnpickup.util.*;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day15 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Droid droid = new Droid(lines.get(0));

                long part1 = droid.part1();
                System.out.println("Part 1: " + part1);
                long part2 = droid.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static final char SPACE = '.';
    static final char WALL = '#';
    static final char OXYGEN = 'O';

    static class Droid {
        final Program program;
        final SparseGrid<Character> grid;
        Coord currentLocation = Coord.ORIGIN;

        Droid(String line) {
            program = new Program(line);
            program.setInputSupplier(this::provideDroidInput);
            program.setOutputConsumer(this::consumeDroidOutput);
            grid = new SparseGrid<>();
            grid.setShowOrigin(true);
        }

        static final Map<Direction, Long> directionsToInputs = new HashMap<>();
        static {
            directionsToInputs.put(Direction.NORTH, 1L);
            directionsToInputs.put(Direction.SOUTH, 2L);
            directionsToInputs.put(Direction.WEST, 3L);
            directionsToInputs.put(Direction.EAST, 4L);
        }
        static final Map<Long, Character> outputsToCells = new HashMap<>();
        static {
            outputsToCells.put(0L, WALL);
            outputsToCells.put(1L, SPACE);
            outputsToCells.put(2L, OXYGEN);
        }

        private final List<Direction> directionsTaken = new ArrayList<>();
        private Direction lastDirection = null;
        private boolean lastWasBacktrack = false;

        long part1() {
            grid.setCell(Coord.ORIGIN, SPACE);
            try {
                program.execute();
            } catch (NoMoreMaze e) {
                System.out.println("Completed search");
            }
            Set<Coord> oxygenCells = grid.findCells(OXYGEN);
            System.out.println(grid);
            if (oxygenCells.size() != 1) throw new RuntimeException("Incorrect number of oxygen sources " + oxygenCells.size());

            MazeSolver mazeSolver = new MazeSolver(grid);
            Set<List<Coord>> routes = mazeSolver.findRoutes();
            return routes.stream().findFirst().map(List::size).orElseThrow(() -> new RuntimeException("No route found"));
        }

        public long part2() {
            Set<Coord> oxygenCells = grid.findCells(OXYGEN);
            Coord oxygenCoord = oxygenCells.stream().findFirst().orElseThrow(() -> new RuntimeException("No oxygen cell"));
            Set<Coord> allSpaces = grid.findCells(SPACE);
            allSpaces.add(oxygenCoord);
            Map<Coord, Integer> distances = new HashMap<>();
            distances.put(oxygenCoord, 0);
            while (!distances.keySet().equals(allSpaces)) {
                Map<Coord, Integer> newDistances = new HashMap<>();
                for (Map.Entry<Coord, Integer> knownDistance : distances.entrySet()) {
                    if (unknown(knownDistance.getKey().north(), allSpaces, distances.keySet())) newDistances.put(knownDistance.getKey().north(), knownDistance.getValue()+1);
                    if (unknown(knownDistance.getKey().south(), allSpaces, distances.keySet())) newDistances.put(knownDistance.getKey().south(), knownDistance.getValue()+1);
                    if (unknown(knownDistance.getKey().east(), allSpaces, distances.keySet())) newDistances.put(knownDistance.getKey().east(), knownDistance.getValue()+1);
                    if (unknown(knownDistance.getKey().west(), allSpaces, distances.keySet())) newDistances.put(knownDistance.getKey().west(), knownDistance.getValue()+1);
                }
                distances.putAll(newDistances);
            }
            return distances.values().stream().max(Integer::compareTo).orElseThrow(() -> new RuntimeException("Can't find max"));
        }

        private boolean unknown(Coord coord, Set<Coord> all, Set<Coord> known) {
            return all.contains(coord) && !known.contains(coord);
        }


        private void consumeDroidOutput(long output) {
            char cell = outputsToCells.get(output);
            Coord outputLocation = currentLocation.move(lastDirection, 1);
            grid.setCell(outputLocation, cell);
            if (cell != WALL) {
                currentLocation = outputLocation;
                if (!lastWasBacktrack) directionsTaken.add(lastDirection);
            }
        }

        private long provideDroidInput() {
            Direction direction = findUnexplored(currentLocation);
            lastWasBacktrack = direction == null;
            if (direction == null) direction = backtrack();
            lastDirection = direction;
            return directionsToInputs.get(direction);
        }

        private Direction backtrack() {
            if (directionsTaken.isEmpty()) throw new NoMoreMaze();
            Direction result = directionsTaken.get(directionsTaken.size()-1).opposite();
            directionsTaken.remove(directionsTaken.size()-1);
            return result;
        }

        private Direction findUnexplored(Coord location) {
            if (!grid.hasCell(location.east())) return Direction.EAST;
            if (!grid.hasCell(location.north())) return Direction.NORTH;
            if (!grid.hasCell(location.south())) return Direction.SOUTH;
            if (!grid.hasCell(location.west())) return Direction.WEST;
            return null;
        }
    }

    static class NoMoreMaze extends RuntimeException {}

    @RequiredArgsConstructor
    static class MazeSolver extends Dijkstra<Coord> {
        private final Grid<Character> grid;

        @Override
        protected Set<Coord> allStates() {
            Set<Coord> result = grid.findCells(SPACE);
            result.addAll(grid.findCells(OXYGEN));
            return result;
        }

        @Override
        protected Coord initialState() {
            return Coord.ORIGIN;
        }

        @Override
        protected Coord targetState() {
            return grid.findCells(OXYGEN).stream().findFirst().orElseThrow(() -> new RuntimeException("No oxygen cell"));
        }

        @Override
        protected long calculateCost(Coord fromState, Coord toState) {
            return 1;
        }

        @Override
        protected boolean statesAreConnected(Coord state1, Coord state2) {
            return state1.isAdjacentTo4(state2);
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }

}
