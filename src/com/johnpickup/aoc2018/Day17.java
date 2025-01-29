package com.johnpickup.aoc2018;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Range;
import com.johnpickup.util.SparseGrid;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day17 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Users/john/Development/AdventOfCode/resources/2018/" + day + "/" + day;
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
                List<CoordinateRange> ranges = stream
                        .filter(s -> !s.isEmpty())
                        .map(CoordinateRange::new)
                        .collect(Collectors.toList());

                Ground ground = new Ground(ranges);

                System.out.println("Part 1: " + ground.part1());
                // 93331 = too high
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Ground {
        private static final char WATER = '|';
        private static final char CLAY = '#';
        private static final char SAND = '.';
        final SparseGrid<Character> grid;
        final int minX;
        final int maxX;
        final int minY;
        final int maxY;

        Ground(List<CoordinateRange> ranges) {
            grid = new SparseGrid<>();
            for (CoordinateRange range : ranges) {
                for (int x = range.xRange.getLower(); x <= range.xRange.getUpper(); x++) {
                    for (int y = range.yRange.getLower(); y <= range.yRange.getUpper(); y++) {
                        grid.setCell(new Coord(x,y), CLAY);
                    }
                }
            }
            minX = grid.findCells('#').stream().map(Coord::getX).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            maxX = grid.findCells('#').stream().map(Coord::getX).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            minY = grid.findCells('#').stream().map(Coord::getY).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            maxY = grid.findCells('#').stream().map(Coord::getY).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
        }

        long part1() {
            //System.out.println(this);
            addWater(new Coord(500, 0).south());
            System.out.println(this);
            return countWater();
        }

        boolean addWater(Coord c) {
            boolean result = c.getY() <= maxY;
            if (getCell(c) != SAND) return result;
            grid.setCell(c, WATER);

            result = result && addWater(c.south());
            result = result && (addWater(c.east()) & addWater(c.west()));

            return result;
        }

        int countWater() {
            return grid.findCells('|').size();
        }

        private char getCell(Coord coord) {
            return Optional.ofNullable(grid.getCell(coord)).orElse('.');
        }

        @Override
        public String toString() {
            return grid.toString();
        }
    }

    @ToString
    static class CoordinateRange {
        final Range<Integer> xRange;
        final Range<Integer> yRange;

        CoordinateRange(String line) {
            String[] parts = line.split(", ");
            xRange = Arrays.stream(parts).filter(s -> s.startsWith("x="))
                    .map(s -> s.substring(2))
                    .map(this::parseRange)
                    .findFirst().orElseThrow(() -> new RuntimeException("unrecognised x range"));
            yRange = Arrays.stream(parts).filter(s -> s.startsWith("y="))
                    .map(s -> s.substring(2))
                    .map(this::parseRange)
                    .findFirst().orElseThrow(() -> new RuntimeException("unrecognised y range"));
        }

        private Range<Integer> parseRange(String s) {
            if (s.contains("..")) {
                String[] parts = s.split("\\.\\.");
                int lower = Integer.parseInt(parts[0]);
                int upper = Integer.parseInt(parts[1]);
                return new Range<>(lower, upper);
            } else {
                int value = Integer.parseInt(s);
                return new Range<>(value, value);
            }
        }
    }
}
