package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 {
    public static void main(String[] args) {
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/Day12/Day12";
        List<String> inputFilenames = Arrays.asList(
                prefix + "-small-test.txt"
                ,prefix + "-test-xo.txt"
                ,prefix + "-test.txt"
                ,prefix + "-test2.txt"
                , prefix + "-test3.txt"
                , prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Garden garden = new Garden(lines);
                long part1 = garden.part1();
                System.out.println("Part 1: " + part1);
                long part2 = garden.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Garden extends CharGrid {
        public Garden(List<String> lines) {
            super(lines);
        }

        long part1() {
            List<Region> regions = extractRegions();
            return regions.stream().map(Region::price).reduce(0L, Long::sum);
        }

        long part2() {
            List<Region> regions = extractRegions();
            return regions.stream().map(Region::price2).reduce(0L, Long::sum);
        }

        private List<Region> extractRegions() {
            List<Region> result = new ArrayList<>();
            for (int y = 0; y < getHeight(); y++) {
                for (int x = 0; x < getWidth(); x++) {
                    Coord coord = new Coord(x, y);
                    char type = getCell(coord);
                    if (!findRegionContaining(result, type, coord).isPresent()) {
                        Set<Coord> connected = findConnected(coord, type, new HashSet<>());
                        Region region = new Region(type);
                        region.addAll(connected);
                        result.add(region);
                    }
                }
            }
            return result;
        }

        private Set<Coord> findConnected(Coord coord, char type, Set<Coord> result) {
            if (!inBounds(coord)) return result;
            if (getCell(coord) != type) return result;
            if (result.contains(coord)) return result;
            result.add(coord);
            result = findConnected(coord.east(), type, result);
            result = findConnected(coord.south(), type, result);
            result = findConnected(coord.west(), type, result);
            result = findConnected(coord.north(), type, result);
            return result;
        }

        private Optional<Region> findRegionContaining(List<Region> regions, char type, Coord coord) {
            return regions.stream()
                    .filter(r -> r.contains(type, coord)).findFirst();
        }
    }

    @RequiredArgsConstructor
    static class Region {
        final Set<Coord> plots = new HashSet<>();
        final char type;

        @Override
        public String toString() {
            return plots.size() + " " + type + " plants "
                    + area() + " * "
                    + perimeter() + " | "
                    + sides()
                    + " = " + price()
                    + " / " + price2();
        }

        public long area() {
            return plots.size();
        }

        public long perimeter() {
            return plots.stream().map(this::emptySidesCount).reduce(0L, Long::sum);
        }

        public long price() {
            return area() * perimeter();
        }

        public long price2() {
            return area() * sides();
        }

        public long sides() {
            return findSides().size();
        }

        long emptySidesCount(Coord c) {
            long result = 0;
            if (!plots.contains(c.north())) result++;
            if (!plots.contains(c.south())) result++;
            if (!plots.contains(c.east())) result++;
            if (!plots.contains(c.west())) result++;

            return result;
        }
        private List<Side> findSides() {
            List<Side> result = new ArrayList<>();
            Set<Coord> northFaces = plots.stream().filter(p -> !plots.contains(p.north())).collect(Collectors.toSet());
            Set<Coord> southFaces = plots.stream().filter(p -> !plots.contains(p.south())).collect(Collectors.toSet());
            Set<Coord> eastFaces = plots.stream().filter(p -> !plots.contains(p.east())).collect(Collectors.toSet());
            Set<Coord> westFaces = plots.stream().filter(p -> !plots.contains(p.west())).collect(Collectors.toSet());
            result.addAll(partitionFaces(northFaces));
            result.addAll(partitionFaces(southFaces));
            result.addAll(partitionFaces(eastFaces));
            result.addAll(partitionFaces(westFaces));
            return result;
        }

        private Set<Side> partitionFaces(Set<Coord> faces) {
            Set<Side> result = new HashSet<>();
            List<Coord> sortedFaces = faces.stream().sorted(Coord::compareTo).collect(Collectors.toList());

            for (Coord face : sortedFaces) {
                // if this face is not adjacent to any in one of the existing Sides results then create a new Side
                Optional<Side> canGoWith = result.stream().filter(s -> s.hasAdjacent(face)).findFirst();
                if (canGoWith.isPresent()) {
                    canGoWith.get().add(face);
                } else {
                    Side newSide = new Side();
                    newSide.add(face);
                    result.add(newSide);
                }
            }
            return result;
        }

        boolean contains(char type, Coord c) {
            return this.type == type && plots.contains(c);
        }

        public void addPlot(Coord coord) {
            plots.add(coord);
        }

        public void addAll(Set<Coord> coords) {
            plots.addAll(coords);
        }
    }

    @EqualsAndHashCode
    static class Side {
        final Set<Coord> coords = new HashSet<>();

        @Override
        public String toString() {
            return "[" + coords.stream().sorted().map(c -> c.toString() + " ").collect(Collectors.joining()) + "]";
        }

        public void addAll(Set<Coord> faces) {
            coords.addAll(faces);
        }
        public void add(Coord face) {
            coords.add(face);
        }
        public void remove(Coord face) {
            coords.remove(face);
        }

        public boolean hasAdjacent(Coord face) {
            return coords.stream().anyMatch(c -> c.isAdjacentTo4(face));
        }
    }
}
