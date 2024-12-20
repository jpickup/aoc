package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;
import com.johnpickup.aoc2024.util.Dijkstra;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day20 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
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

                Racetrack racetrack = new Racetrack((lines));
                System.out.println(racetrack);

                long part1 = racetrack.part1();
                System.out.println("Part 1: " + part1);
                long part2 = racetrack.part2();
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
    static class Racetrack {
        final CharGrid grid;
        final Coord start;
        final Coord end;
        Racetrack(List<String> lines) {
            grid = new CharGrid(lines);
            start = grid.findCharAndCleanup(START, SPACE);
            end = grid.findCharAndCleanup(END, SPACE);
        }

        public long part1() {
            return findCheatsWithSaving(isTest ? 2 : 100, 2).size();
        }

        public long part2() {
            List<Cheat> cheats = findCheatsWithSaving(isTest ? 50 : 100, 20);
//            System.out.println("# cheats = " + cheats.size());
//            Map<Long, Long> bySaving = cheats.stream().collect(Collectors.groupingBy(Cheat::saving, Collectors.counting()));
//            System.out.println(bySaving.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> "" + e.getKey() + " -> " + e.getValue() + "\n").collect(Collectors.toList()));
            return cheats.size();
        }

        public List<Cheat> findCheatsWithSaving(int targetSaving, int cheatLength) {
            RacetrackSolver racetrackSolver = new RacetrackSolver(this);
            Set<List<Coord>> routes = racetrackSolver.findRoutes();
            List<Coord> route = routes.stream().findFirst().orElseThrow(() -> new RuntimeException("No route found"));
            List<Cheat> cheats = findCheats(route, cheatLength, targetSaving);
            return cheats.stream().filter(c -> c.saving() >= targetSaving).collect(Collectors.toList());
        }

        private List<Cheat> findCheats(List<Coord> route, int cheatLength, int targetSaving) {
            List<Coord> routeWithStart = new ArrayList<>(route);
            routeWithStart.add(0, start);
            List<Cheat> result = new ArrayList<>();
            for (int i = 0; i < routeWithStart.size()-1; i++) {
                for (int j = i+1; j < routeWithStart.size(); j++) {
                    Coord curr = routeWithStart.get(i);
                    Coord next = routeWithStart.get(j);
                    List<Coord> removed = routeWithStart.subList(i + 1, j);
                    if (isUsefulJump(curr, next, cheatLength, removed, targetSaving)) {
                        Cheat cheat = new Cheat(routeWithStart, removed, curr, next);
                        result.add(cheat);
                    }
                }
            }
            return result;
        }

        private boolean isUsefulJump(Coord c1, Coord c2, int distance, List<Coord> removed, int targetSaving) {
            int basicDist = c1.distanceFrom(c2);
            if (basicDist > distance) return false;
            int saving = removed.size() - c1.distanceFrom(c2) + 1;
            return saving >= targetSaving;
        }

        @Override
        public String toString() {
            return grid.toString();
        }
    }

    @Data
    static class Cheat {
        final List<Coord> route;
        final List<Coord> removed;
        final Coord start;
        final Coord end;
        long saving() {
            int dist = Math.abs(end.getX() - start.getX()) + Math.abs(end.getY() - start.getY());
            return removed.size()   // steps removed
                    - dist + 1;     // steps taken
        }
    }

    @RequiredArgsConstructor
    static class RacetrackSolver extends Dijkstra<Coord> {
        final Racetrack racetrack;

        @Override
        protected Set<Coord> allStates() {
            return racetrack.grid.findAll(SPACE);
        }

        @Override
        protected Coord initialState() {
            return racetrack.start;
        }

        @Override
        protected Coord targetState() {
            return racetrack.end;
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
