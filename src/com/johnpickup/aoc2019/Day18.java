package com.johnpickup.aoc2019;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import com.johnpickup.util.DijkstraWithoutFullState;
import com.johnpickup.util.Sets;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day18 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + "-test3.txt"
                , prefix + "-test4.txt"
                , prefix + "-test5.txt"
//                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Maze maze = new Maze(lines);
                System.out.println(maze);

                long part1 = maze.part1();
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }


    static class Maze {
        final CharGrid grid;
        Maze(List<String> lines) {
            grid = new CharGrid(lines);
        }

        @Override
        public String toString() {
            return grid.toString();
        }

        public long part1() {
            MazeSolver mazeSolver = new MazeSolver(grid);
            //System.out.println(mazeSolver);

            Set<List<MazeState>> solutions = mazeSolver.findRoutes();
            //System.out.println(solutions);

            return mazeSolver.lowestCost();
        }
    }

    @ToString(exclude = "grid")
    static class MazeSolver extends DijkstraWithoutFullState<MazeState> {
        final CharGrid grid;
        final Map<Character, Coord> keys;
        final Map<Character, Coord> doors;
        final Set<Coord> spaces;
        final Coord entry;

        MazeSolver(CharGrid grid) {
            this.grid = grid;
            spaces = new HashSet<>();
            spaces.addAll(grid.findCells('.'));
            keys = findKeys();
            doors = findDoors();
            spaces.addAll(keys.values());
            spaces.addAll(doors.values());
            entry = Optional.ofNullable(findChar('@')).orElseThrow(() -> new RuntimeException("Failed to find the entry"));
            spaces.add(entry);
            //buildConnections();
        }


        private Map<Character, Coord> findDoors() {
            Map<Character, Coord> result = new HashMap<>();
            for (char ch = 'A'; ch <= 'Z'; ch++) {
                Coord coord = findChar(ch);
                if (coord != null) result.put(ch, coord);
            }
            return result;
        }

        private Map<Character, Coord> findKeys() {
            Map<Character, Coord> result = new HashMap<>();
            for (char ch = 'a'; ch <= 'z'; ch++) {
                Coord coord = findChar(ch);
                if (coord != null) result.put(ch, coord);
            }
            return result;
        }

        private Coord findChar(char ch) {
            return grid.findCells(ch).stream().findFirst().orElse(null);
        }

        @Override
        protected MazeState initialState() {
            return new MazeState(entry, Collections.emptySet());
        }

        @Override
        protected MazeState targetState() {
            throw new RuntimeException("Not implemented");
        }

        @Override
        protected boolean isTargetState(MazeState state) {
            return state.keys.equals(keys.keySet());        // we hold all the keys, in any location
        }

        @Override
        protected long calculateCost(MazeState fromState, MazeState toState) {
            return fromState.location.equals(toState.location) ? 0L : 1L;
        }

        @Override
        protected Set<MazeState> adjacentStates(MazeState mazeState) {
            Set<MazeState> result = new HashSet<>();
            result.addAll(generateStatesForLocationAndKeys(mazeState.location, mazeState.keys));
            result.addAll(generateStatesForLocationAndKeys(mazeState.location.north(), mazeState.keys));
            result.addAll(generateStatesForLocationAndKeys(mazeState.location.south(), mazeState.keys));
            result.addAll(generateStatesForLocationAndKeys(mazeState.location.east(), mazeState.keys));
            result.addAll(generateStatesForLocationAndKeys(mazeState.location.west(), mazeState.keys));
            return result.stream().filter(s -> statesAreConnected(s, mazeState)).collect(Collectors.toSet());
        }

        private Collection<MazeState> generateStatesForLocationAndKeys(Coord location, Set<Character> keys) {
            if (!spaces.contains(location)) return Collections.emptySet();
            Set<Set<Character>> validKeySets = generateValidKeySets(keys, location);
            return validKeySets.stream().map(ks -> new MazeState(location, ks)).collect(Collectors.toSet());
        }

        private Set<Set<Character>> generateValidKeySets(Set<Character> keys, Coord location) {
            Character cell = grid.getCell(location);
            if (cell == '.' || cell == '@') return Collections.singleton(keys);
            if (cell >= 'a' && cell <= 'z') return Sets.union(Collections.singleton(keys), Collections.singleton(Sets.union(keys, Collections.singleton(cell))));
            if (cell >= 'A' && cell <= 'Z') return Sets.union(Collections.singleton(keys), Collections.singleton(Sets.union(keys, Collections.singleton(cell))));
            return Collections.emptySet();
        }

        private final Map<StatePair, Boolean> areStatesConnected = new HashMap<>();

        @Override
        protected boolean statesAreConnected(MazeState toState, MazeState fromState) {
            StatePair pair = new StatePair(fromState, toState);
            areStatesConnected.putIfAbsent(pair, calcStatesAreConnected(fromState, toState));
            return areStatesConnected.get(pair);
        }

        protected boolean calcStatesAreConnected(MazeState fromState, MazeState toState) {
            boolean adjacent = fromState.location.isAdjacentTo4(toState.location) && fromState.keys.equals(toState.keys);
            boolean obtains = false;
            // moving to cell with a key gives us the key
            if (fromState.location.equals(toState.location) && keys.containsValue(fromState.location)) {
                char keyAtLocation = keys.entrySet().stream()
                        .filter(e -> e.getValue().equals(fromState.location))
                        .map(Map.Entry::getKey)
                        .findFirst().orElseThrow(() -> new RuntimeException("Failed to find key"));
                Set<Character> fromStateKeys = fromState.keys;
                Set<Character> toStateKeys = new HashSet<>(fromStateKeys);
                toStateKeys.add(keyAtLocation);
                obtains = !fromStateKeys.contains(keyAtLocation) && toState.keys.equals(toStateKeys);
            }
            // can only leave a cell with a key if we have taken the key
            boolean collectsKey = true;
            if (adjacent && keys.containsValue(fromState.location)) {
                char keyAtLocation = keys.entrySet().stream()
                        .filter(e -> e.getValue().equals(fromState.location))
                        .map(Map.Entry::getKey)
                        .findFirst().orElseThrow(() -> new RuntimeException("Failed to find key"));
                collectsKey = toState.keys.contains(Character.toLowerCase(keyAtLocation));
            }
            // can only enter a cell with a door if we have the key
            boolean hasKeyForDoor = true;
            if (doors.containsValue(toState.location)) {
                char doorAtNewLocation = doors.entrySet().stream()
                        .filter(e -> e.getValue().equals(toState.location))
                        .map(Map.Entry::getKey)
                        .findFirst().orElseThrow(() -> new RuntimeException("Failed to find door"));
                hasKeyForDoor = fromState.keys.contains(Character.toLowerCase(doorAtNewLocation));
            }

            return (adjacent || obtains) && collectsKey && hasKeyForDoor;
        }

        @Override
        protected boolean findAllRoutes() {
            return false;
        }
    }

    @Data
    @RequiredArgsConstructor
    static class MazeState {
        final Coord location;
         final Set<Character> keys;
         @Override
        public String toString() {
             return String.format("%s/%s", location, keys);
         }
    }

    @Data
    @RequiredArgsConstructor
    static class StatePair {
        final MazeState fromState;
        final MazeState toState;
    }
}

