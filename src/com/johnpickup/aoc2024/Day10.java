package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.Coord;
import com.johnpickup.aoc2024.util.IntGrid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {
    public static void main(String[] args) {
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/Day10/Day10";
        List<String> inputFilenames = Arrays.asList(prefix + "-test-small.txt"
                , prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

                ElevationGrid elevationGrid = new ElevationGrid(lines);
                System.out.println(elevationGrid);

                long part1 = elevationGrid.part1();
                System.out.println("Part 1: " + part1);
                long part2 = elevationGrid.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class ElevationGrid extends IntGrid {
        ElevationGrid(List<String> lines) {
            super(lines);
        }

        public long part1() {
            Set<Coord> trailHeads = findWithElevation(0);
            Map<Coord, List<List<Coord>>> allPaths = new HashMap<>();
            for (Coord trailHead : trailHeads) {
                List<List<Coord>> paths = findPaths(trailHead);
                allPaths.put(trailHead, paths);
            }
            // number of 9 points that can be reached for each trailhead
            Map<Coord, Integer> ninesCounts = new HashMap<>();
            for (Map.Entry<Coord, List<List<Coord>>> entry : allPaths.entrySet()) {
                ninesCounts.put(entry.getKey(),
                        entry.getValue().stream().map(p -> p.get(p.size() - 1)).collect(Collectors.toSet()).size());
            }
            return ninesCounts.values().stream().reduce(0, Integer::sum);
        }

        public long part2() {
            Set<Coord> trailHeads = findWithElevation(0);
            Map<Coord, List<List<Coord>>> allPaths = new HashMap<>();
            for (Coord trailHead : trailHeads) {
                List<List<Coord>> paths = findPaths(trailHead);
                allPaths.put(trailHead, paths);
            }
            // number of paths from each trailhead
            Map<Coord, Integer> pathCounts = new HashMap<>();
            for (Map.Entry<Coord, List<List<Coord>>> entry : allPaths.entrySet()) {
                pathCounts.put(entry.getKey(), entry.getValue().size());
            }
            return pathCounts.values().stream().reduce(0, Integer::sum);
        }

        private List<List<Coord>> findPaths(Coord trailHead) {
            List<Coord> initialPath = Collections.singletonList(trailHead);
            List<List<Coord>> result = Collections.singletonList(initialPath);

            for (int elev = 1; elev <= 9; elev++) {
                result = findPaths(result, elev);
            }
            return result;
        }

        private List<List<Coord>> findPaths(List<List<Coord>> paths, int elev) {
            List<List<Coord>> result = new ArrayList<>();
            for (List<Coord> path : paths) {
                Coord end = path.get(path.size() - 1);
                if (getCell(end.north()) == elev) {
                    List<Coord> newPath = new ArrayList<>(path);
                    newPath.add(end.north());
                    result.add(newPath);
                }
                if (getCell(end.south()) == elev) {
                    List<Coord> newPath = new ArrayList<>(path);
                    newPath.add(end.south());
                    result.add(newPath);
                }
                if (getCell(end.east()) == elev) {
                    List<Coord> newPath = new ArrayList<>(path);
                    newPath.add(end.east());
                    result.add(newPath);
                }
                if (getCell(end.west()) == elev) {
                    List<Coord> newPath = new ArrayList<>(path);
                    newPath.add(end.west());
                    result.add(newPath);
                }
            }
            return result;
        }

        private Set<Coord> findWithElevation(int elev) {
            Set<Coord> result = new HashSet<>();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Coord coord = new Coord(x, y);
                    if (getCell(coord) == elev) result.add(coord);
                }
            }
            return result;
        }
    }
}
