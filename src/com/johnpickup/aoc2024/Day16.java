package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day16 {
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                ,prefix + "-test2.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Grid grid = new Grid(lines);

                long part1 = grid.part1();
                System.out.println("Part 1: " + part1);
                long part2 = grid.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static final char START = 'S';
    static final char END = 'E';
    static final char SPACE = '.';
    static final char WALL = '#';

    static final long ROTATION_COST = 1000L;
    static final long FORWARD_COST = 1L;

    static class Grid extends CharGrid {
        Coord start;
        Coord end;

        public Grid(List<String> lines) {
            super(lines);
            start = findCharAndCleanup(START, SPACE);
            end = findCharAndCleanup(END, SPACE);
        }

        long part1() {
            List<Step> route = findRoute();
            return route.stream().map(s -> s.movement.cost).reduce(0L, Long::sum);
        }

        long part2() {
            Set<List<Step>> routes = findRoutes();
            Set<Coord> pointsOnRoutes = new HashSet<>();
            pointsOnRoutes.add(start);
            pointsOnRoutes.add(end);
            for (List<Step> route : routes) {
                pointsOnRoutes.addAll(
                    route.stream().map(s -> s.newLocation).collect(Collectors.toSet())
                );
            }
            return pointsOnRoutes.size();
        }

        Set<List<Step>> cachedRoutes = null;    // part 1 & part 2 use the same data - just cache it

        List<Step> findRoute() {
            return findRoutes().stream().findFirst().orElseThrow(() -> new RuntimeException("No route found"));
        }

        Set<List<Step>> findRoutes() {
            if (cachedRoutes != null) return cachedRoutes;
            Map<State, Long> unvisited = new HashMap<>();
            Map<State, Long> visited = new HashMap<>();
            Map<State, Set<List<Step>>> paths = new HashMap<>();
            Set<Coord> spaces = findAll(SPACE);
            for (Coord space : spaces) {
                unvisited.put(new State(space, Direction.NORTH), Long.MAX_VALUE);
                unvisited.put(new State(space, Direction.SOUTH), Long.MAX_VALUE);
                unvisited.put(new State(space, Direction.EAST), Long.MAX_VALUE);
                unvisited.put(new State(space, Direction.WEST), Long.MAX_VALUE);
            }
            State initialState = new State(start, Direction.EAST);
            unvisited.put(initialState, 0L);
            paths.put(initialState, Collections.singleton(Collections.emptyList()));

            Map.Entry<State, Long> lowestCostState = findSmallest(unvisited);

            while (lowestCostState != null) {
                Map<State, Long> neighbours = findNeighbours(unvisited, lowestCostState.getKey());
                for (Map.Entry<State, Long> entry : neighbours.entrySet()) {
                    long cost = lowestCostState.getValue() + calcCost(lowestCostState.getKey(), entry.getKey());
                    if (cost <= entry.getValue()) {
                        Set<List<Step>> possibleStepsToState = paths.get(lowestCostState.getKey());

                        Set<List<Step>> possibleStepsToEntry = Optional.ofNullable(paths.get(entry.getKey())).orElse(new HashSet<>());
                        for (List<Step> stepsToState : possibleStepsToState) {
                            List<Step> stepsToEntry = calcPath(stepsToState, lowestCostState.getKey(), entry.getKey());
                            possibleStepsToEntry.add(stepsToEntry);
                        }
                        unvisited.put(entry.getKey(), cost);
                        paths.put(entry.getKey(), possibleStepsToEntry);
                    }
                    visited.put(lowestCostState.getKey(), lowestCostState.getValue());
                }
                unvisited.remove(lowestCostState.getKey());
                lowestCostState = findSmallest(unvisited);
            }

            Map.Entry<State, Long> finish = findFinish(visited);
            Set<List<Step>> possibleSteps = paths.get(finish.getKey());

            cachedRoutes = possibleSteps;
            return possibleSteps;
        }

        private long calcCost(State state1, State state2) {
            if (state1.coord.equals(state2.coord)) return ROTATION_COST;
            return FORWARD_COST;
        }

        private List<Step> calcPath(List<Step> from, State state1, State state2) {
            List<Step> result = new ArrayList<>(from);
            if (state1.coord.equals(state2.coord)) {
                if (state1.direction.left().equals(state2.direction))
                    result.add(new Step(state2.coord, Movement.TURN_LEFT));
                else
                    result.add(new Step(state2.coord, Movement.TURN_RIGHT));
            } else {
                result.add(new Step(state2.coord, Movement.FORWARD));
            }
            return result;
        }

        private Map<State, Long> findNeighbours(Map<State, Long> unvisited, State state) {
            Map<State, Long> result = new HashMap<>();
            for (Map.Entry<State, Long> entry : unvisited.entrySet()) {
                State nextState = entry.getKey();
                // a rotation staying at the same coord
                if (isRotationAdjacent(state, nextState)) {
                    result.put(nextState, entry.getValue());
                }
                // a movement from the coord in the direction faced
                if (isLocationAdjacent(state, nextState)) {
                    result.put(nextState, entry.getValue());
                }
            }
            return result;
        }

        private boolean isRotationAdjacent(State state1, State state2) {
            return state1.coord.equals(state2.coord) &&
                    ((state1.direction.left().equals(state2.direction))
                    || (state1.direction.right().equals(state2.direction)));
        }

        private boolean isLocationAdjacent(State state1, State state2) {
            Coord movementCoord = state1.direction.apply(state1.coord);
            return movementCoord.equals(state2.coord) && state1.direction.equals(state2.direction);
        }

        private Map.Entry<State, Long> findSmallest(Map<State, Long> states) {
            Map.Entry<State, Long> smallest = null;
            for (Map.Entry<State, Long> entry : states.entrySet()) {
                if (entry.getValue() < Optional.ofNullable(smallest).map(Map.Entry::getValue).orElse(Long.MAX_VALUE)) {
                    smallest = entry;
                }
            }
            return smallest;
        }

        private Map.Entry<State, Long> findFinish(Map<State, Long> states) {
            Map.Entry<State, Long> smallest = null;
            for (Map.Entry<State, Long> entry : states.entrySet()) {
                if (entry.getKey().coord.equals(end) && entry.getValue() < Optional.ofNullable(smallest).map(Map.Entry::getValue).orElse(Long.MAX_VALUE)) {
                    smallest = entry;
                }
            }
            return smallest;
        }
    }

    @RequiredArgsConstructor
    enum Movement {
        FORWARD(FORWARD_COST),
        TURN_LEFT(ROTATION_COST),
        TURN_RIGHT(ROTATION_COST);
        final long cost;
    }

    @RequiredArgsConstructor
    @Data
    static class Step {
        final Coord newLocation;
        final Movement movement;
    }

    @RequiredArgsConstructor
    enum Direction {
        NORTH('^'),
        SOUTH('v'),
        EAST('<'),
        WEST('>');
        final char ch;

        Direction left() {
            switch (this) {
                case NORTH: return WEST;
                case WEST: return SOUTH;
                case SOUTH: return EAST;
                case EAST: return NORTH;
                default: throw new RuntimeException("Unknown dir " +  this);
            }
        }
        Direction right() {
            switch (this) {
                case NORTH: return EAST;
                case EAST: return SOUTH;
                case SOUTH: return WEST;
                case WEST: return NORTH;
                default: throw new RuntimeException("Unknown dir " +  this);
            }
        }
        Coord apply(Coord c) {
            switch (this) {
                case NORTH: return c.north();
                case EAST: return c.east();
                case SOUTH: return c.south();
                case WEST: return c.west();
                default:
                    throw new RuntimeException("Unknown dir " + this);
            }
        }
    }

    @RequiredArgsConstructor
    @Data
    @EqualsAndHashCode
    static class State {
        final Coord coord;
        final Direction direction;
    }
}
