package com.johnpickup.aoc2019;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day24 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
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

                Bugs bugs = new Bugs(lines);

                System.out.println("Part 1: " + bugs.part1());
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Bugs {
        private static final char LIVING = '#';
        private static final char DEAD = '.';
        final Set<CharGrid> layouts;
        final CharGrid initialLayout;

        Bugs(List<String> lines) {
            initialLayout = new CharGrid(lines);
            layouts = new HashSet<>();
        }

        public long part1() {
            CharGrid currentLayout = initialLayout;
            int time = 0;
            do {
                time ++;
                layouts.add(currentLayout);
                currentLayout = nextGeneration(currentLayout);
//                System.out.println("Time: " + time);
//                System.out.println(currentLayout);
//                System.out.println();
            } while (!layouts.contains(currentLayout));
            System.out.println(currentLayout);
            return calcBioDiversity(currentLayout);
        }

        private CharGrid nextGeneration(CharGrid grid) {
            CharGrid result = new CharGrid(grid);
            for (int x = 0; x < grid.getWidth(); x++) {
                for (int y = 0; y < grid.getHeight(); y++) {
                    Coord c = new Coord(x, y);
                    result.setCell(c, isNextGenerationAlive(grid, c) ? LIVING : DEAD);
                }
            }
            return result;
        }

        private boolean isNextGenerationAlive(CharGrid grid, Coord c) {
            int adjacentCount = adjacentLivingCount(grid, c);
            if (grid.getCell(c) == LIVING)
                // A bug dies (becoming an empty space) unless there is exactly one bug adjacent to it.
                return adjacentCount == 1;
            else
                // An empty space becomes infested with a bug if exactly one or two bugs are adjacent to it.
                return adjacentCount >= 1 && adjacentCount <= 2;
        }

        private int adjacentLivingCount(CharGrid grid, Coord c) {
            return (grid.getCell(c.north()) == LIVING ? 1 : 0)
                    + (grid.getCell(c.south()) == LIVING ? 1 : 0)
                    + (grid.getCell(c.east()) == LIVING ? 1 : 0)
                    + (grid.getCell(c.west()) == LIVING ? 1 : 0)
                    ;
        }

        private long calcBioDiversity(CharGrid grid) {
            long result = 0L;
            long n = 0;
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Coord c = new Coord(x, y);
                    if (grid.getCell(c) == LIVING) {
                        result += Math.pow(2, n);
                    }
                    n++;
                }
            }
            return result;
        }
    }
}
