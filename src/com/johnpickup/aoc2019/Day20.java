package com.johnpickup.aoc2019;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.Dijkstra;
import com.johnpickup.util.DijkstraWithoutFullState;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day20 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + "-test3.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Maze maze = new Maze(lines);
                System.out.println("Part 1: " + maze.part1());
                System.out.println("Part 2: " + maze.part2());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }
    static class Maze {
        final CharGrid grid;
        Map<String, Set<Coord>> portals;
        final List<PortalPair> portalPairs;
        final Coord start;
        final Coord end;
        Maze(List<String> lines) {
            grid = new CharGrid(lines);
            portals = findPortals();
            System.out.println("Portals: " + portals);
            portalPairs = portals.values().stream().filter(v -> v.size()==2).map(PortalPair::from).collect(Collectors.toList());
            System.out.println(portalPairs);
            start = portals.get("AA").stream().findFirst().orElseThrow(() -> new RuntimeException("No Start"));
            end = portals.get("ZZ").stream().findFirst().orElseThrow(() -> new RuntimeException("No End"));
            System.out.println("Start = " + start);
            System.out.println("End = " + end);
        }

        private Map<String, Set<Coord>> findPortals() {
            Map<String, Set<Coord>> result = new HashMap<>();
            // get locations of every portal character
            Map<Coord, Character> charLocations = new HashMap<>();
            for (char c = 'A'; c <= 'Z'; c++) {
                for (Coord coord : grid.findCells(c)) {
                    charLocations.put(coord, c);
                }
            }

            // find every pair of adjacent characters
            for (Map.Entry<Coord, Character> char1 : charLocations.entrySet()) {
                for (Map.Entry<Coord, Character> char2 : charLocations.entrySet()) {
                    if (char1.getKey().isAdjacentTo4(char2.getKey())
                            && (char1.getKey().getX() <= char2.getKey().getX() || char1.getKey().getY() <= char2.getKey().getY())) {
                        int dx = char1.getKey().getX() - char2.getKey().getX();
                        int dy = char1.getKey().getY() - char2.getKey().getY();
                        Coord side1 = new Coord(char1.getKey().getX() + dx, char1.getKey().getY() + dy);
                        Coord side2 = new Coord(char2.getKey().getX() - dx, char2.getKey().getY() - dy);
                        Coord portalCoord;
                        if (grid.getCell(side1).equals('.')) {
                            portalCoord = side1;
                        } else if (grid.getCell(side2).equals('.')) {
                            portalCoord = side2;
                        } else throw new RuntimeException("Failed to match the pair with a space");
                        String label = "" + char1.getValue() + char2.getValue();
                        result.putIfAbsent(label, new HashSet<>());
                        result.get(label).add(portalCoord);
                    }
                }
            }
            return result;
        }

        public long part1() {
            MazeSolver mazeSolver = new MazeSolver(this);
            return mazeSolver.lowestCost();
        }

        public long part2() {
            MazeSolverPart2 mazeSolver = new MazeSolverPart2(this);
            return mazeSolver.lowestCost();
        }

        @Override
        public String toString() {
            return grid.toString();
        }

        public boolean hasPortal(Coord from, Coord to) {
            return portalPairs.stream().anyMatch(pair -> pair.connectsPoints(from, to));
        }

        public boolean isOuterPortal(Coord coord) {
            return coord.getX() <= 2 || coord.getX() > grid.getWidth()-3
                    || coord.getY() <= 2 || coord.getY() > grid.getHeight()-3;
        }

        public boolean isInnerPortal(Coord coord) {
            return !isOuterPortal(coord);
        }
    }

    @RequiredArgsConstructor
    static class MazeSolver extends Dijkstra<Coord> {
        private final Maze maze;

        @Override
        protected Set<Coord> allStates() {
            return maze.grid.findCells('.');
        }

        @Override
        protected Coord initialState() {
            return maze.start;
        }

        @Override
        protected Coord targetState() {
            return maze.end;
        }

        @Override
        protected long calculateCost(Coord fromState, Coord toState) {
            return 1;
        }

        @Override
        protected boolean statesAreConnected(Coord toState, Coord fromState) {
            return toState.isAdjacentTo4(fromState)
                    || maze.hasPortal(fromState, toState);
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }

    @RequiredArgsConstructor
    static class MazeSolverPart2 extends DijkstraWithoutFullState<CoordAtLevel> {
        private final Maze maze;

        @Override
        protected Set<CoordAtLevel> adjacentStates(CoordAtLevel state) {
            Set<CoordAtLevel> result = new HashSet<>();
            addStateIfConnected(result, state, new CoordAtLevel(state.coord.north(), state.level));
            addStateIfConnected(result, state, new CoordAtLevel(state.coord.south(), state.level));
            addStateIfConnected(result, state, new CoordAtLevel(state.coord.east(), state.level));
            addStateIfConnected(result, state, new CoordAtLevel(state.coord.west(), state.level));
            addStateIfConnected(result, state, new CoordAtLevel(state.coord, state.level+1));
            addStateIfConnected(result, state, new CoordAtLevel(state.coord, state.level-1));
            return result;
        }

        private void addStateIfConnected(Set<CoordAtLevel> result, CoordAtLevel from, CoordAtLevel to) {
            if (statesAreConnected(to, from)) result.add(to);
        }

        @Override
        protected CoordAtLevel initialState() {
            return new CoordAtLevel(maze.start, 0);
        }

        @Override
        protected CoordAtLevel targetState() {
            return new CoordAtLevel(maze.end, 0);
        }

        @Override
        protected long calculateCost(CoordAtLevel fromState, CoordAtLevel toState) {
            return 1;
        }

        @Override
        protected boolean statesAreConnected(CoordAtLevel toState, CoordAtLevel fromState) {
            return (toState.coord.isAdjacentTo4(fromState.coord) && toState.level == fromState.level)
                    || hasPortal(fromState, toState);
        }

        private boolean hasPortal(CoordAtLevel fromState, CoordAtLevel toState) {
            if (!maze.hasPortal(fromState.coord, toState.coord)) return false;
            // figure out the levels
            boolean goingDown = maze.isOuterPortal(fromState.coord) && maze.isInnerPortal(toState.coord);
            boolean goingUp = maze.isInnerPortal(fromState.coord) && maze.isOuterPortal(toState.coord);

            return (goingDown && fromState.level+1 == toState.level)
                    || (goingUp && fromState.level-1 == toState.level);
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }

    @Data
    static class CoordAtLevel {
        final Coord coord;
        final int level;
    }

    @Data
    @RequiredArgsConstructor
    static class PortalPair {
        final Coord point1;
        final Coord point2;

        static PortalPair from(Collection<Coord> coords) {
            if (coords == null || coords.size() != 2) throw new RuntimeException("Can't make a pair from " + coords);
            List<Coord> coordList = new ArrayList<>(coords);
            return new PortalPair(coordList.get(0), coordList.get(1));
        }

        boolean connectsPoints(Coord p1, Coord p2) {
            return (point1.equals(p1) && point2.equals(p2))
                    || (point1.equals(p2) && point2.equals(p1));
        }
    }
}
