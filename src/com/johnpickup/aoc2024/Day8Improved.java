package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 Whilst writing the first version it was clear there was a much more efficient solution, so I gave it a try.
 */
public class Day8Improved {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2024/Day8/Day8.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            Grid grid = new Grid(lines);
            System.out.println("Part 1 : " + grid.part1());
            System.out.println("Part 2 : " + grid.part2());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    static class Grid {
        final int width;
        final int height;
        final Map<Character, Set<Coord>> antennasByFrequency = new HashMap<>();
        Grid(List<String> lines) {
            width = lines.get(0).length();
            height = lines.size();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char c = lines.get(y).charAt(x);
                    if (c != '.') {
                        antennasByFrequency.putIfAbsent(c, new HashSet<>());
                        antennasByFrequency.get(c).add(new Coord(x, y));
                    }
                }
            }
        }

        int part1() {
            Set<Coord> result = new HashSet<>();
            for (Map.Entry<Character, Set<Coord>> antennasEntry : antennasByFrequency.entrySet()) {
                Set<Coord> antiNodes = createAntiNodes(antennasEntry.getValue());
                result.addAll(antiNodes);
            }
            return result.size();
        }

        int part2() {
            Set<Coord> result = new HashSet<>();
            for (Map.Entry<Character, Set<Coord>> antennasEntry : antennasByFrequency.entrySet()) {
                Set<Coord> antiNodes = createPart2AntiNodes(antennasEntry.getValue());
                result.addAll(antiNodes);
            }
            return result.size();
        }

        private Set<Coord> createAntiNodes(Set<Coord> nodes) {
            Set<Coord> result = new HashSet<>();
            for (Coord node1 : nodes) {
                for (Coord node2 : nodes) {
                    if (!node1.equals(node2)) {
                        result.addAll(createAntiNodePair(node1, node2));
                    }
                }
            }
            return result;
        }

        private Set<Coord> createAntiNodePair(Coord node1, Coord node2) {
            int dx = node2.x - node1.x;
            int dy = node2.y - node1.y;
            Coord a1 = new Coord(node1.x - dx, node1.y - dy);
            Coord a2 = new Coord(node2.x + dx, node2.y + dy);
            return Stream.of(a1, a2).filter(this::inBounds).collect(Collectors.toSet());
        }

        private Set<Coord> createPart2AntiNodes(Set<Coord> nodes) {
            Set<Coord> result = new HashSet<>();
            for (Coord node1 : nodes) {
                for (Coord node2 : nodes) {
                    if (!node1.equals(node2)) {
                        result.addAll(createMultipleAntiNodes(node1, node2));
                    }
                }
            }
            return result;
        }

        private Set<Coord> createMultipleAntiNodes(Coord node1, Coord node2) {
            Set<Coord> result = new HashSet<>();
            int dx = node2.x - node1.x;
            int dy = node2.y - node1.y;

            Coord n = new Coord(node1);
            while (inBounds(n)) {
                result.add(n);
                n = new Coord(n.x + dx, n.y + dy);
            }

            n = new Coord(node2);
            while (inBounds(n)) {
                result.add(n);
                n = new Coord(n.x - dx, n.y - dy);
            }

            return result;
        }

        private boolean inBounds(Coord coord) {
            return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height;
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;

        public Coord(Coord c) {
            this(c.x, c.y);
        }
    }
}
