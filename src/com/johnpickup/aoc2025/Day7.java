package com.johnpickup.aoc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day7 {
    static boolean isTest;

    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object() {
        });
        for (String inputFilename : inputFilenames) {

            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                Manifold manifold = new Manifold(lines);
                manifold.solve();
                long part1 = manifold.part1();
                System.out.println("Part 1: " + part1);
                long part2 = manifold.part2();
                System.out.println("Part 2: " + part2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Manifold {
        static final char SPLITTER = '^';
        static final char SPACE = '.';
        static final char START = 'S';
        static final char BEAM = '|';
        private final CharGrid grid;

        Manifold(List<String> lines) {
            grid = new CharGrid(lines);
        }

        long countSplits = 0;
        long countTimelines = 0;

        void solve() {
            Coord start = grid.findCells(START).stream().findFirst().orElseThrow();
            TreeMap<Coord, Long> locations = new TreeMap<>();
            locations.put(start, 1L);
            while (beforeBottom(locations.keySet())) {
                locations = iterateLevel(locations);
//                for (Map.Entry<Coord, Long> entry : locations.entrySet()) {
//                    char beamCount = entry.getValue() > 9 ? '|' : (char) ('0' + (entry.getValue() % 10));
//                    grid.setCell(entry.getKey(), beamCount);
//                }
//                System.out.println();
//                System.out.println(grid);
//                System.out.printf("%d%n", locations.values().stream().reduce(0L, Long::sum));
            }
            countTimelines = locations.values().stream().reduce(0L, Long::sum);
        }

        private TreeMap<Coord, Long> iterateLevel(TreeMap<Coord, Long> locations) {
            TreeMap<Coord, Long> result = new TreeMap<>();
            for (Map.Entry<Coord, Long> entry : locations.entrySet()) {
                Coord next = entry.getKey().south();
                if (grid.getCell(next) == SPLITTER) {
                    addAndCount(result, next.east(), entry.getValue());
                    addAndCount(result, next.west(), entry.getValue());
                    countSplits++;
                } else {
                    addAndCount(result, next, entry.getValue());
                }
            }
            return result;
        }

        private void addAndCount(TreeMap<Coord, Long> map, Coord coord, long count) {
            if (map.containsKey(coord)) {
                map.put(coord, map.get(coord) + count);
            } else {
                map.put(coord, count);
            }
        }

        private boolean beforeBottom(Set<Coord> locations) {
            return locations.stream().findFirst().orElseThrow().getY() < grid.getHeight() - 1;
        }

        public long part1() {
            return countSplits;
        }
        public long part2() {
            return countTimelines;
        }
    }
}
