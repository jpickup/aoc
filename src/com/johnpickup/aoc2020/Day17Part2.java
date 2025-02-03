package com.johnpickup.aoc2020;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord4D;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day17Part2 {
    static boolean isTest;

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2020/Day17/Day17";
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

                ConwayGrid conwayGrid = new ConwayGrid(lines);
                System.out.println(conwayGrid);

                for (int i = 0; i < 6; i++) {
                    conwayGrid = conwayGrid.iterate();
//                    System.out.println("Iteration " + i+1 + " -----------------------------------------");
//                    System.out.println(conwayGrid);
                }

                System.out.println("Part 2: " + conwayGrid.part2());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class ConwayGrid {
        final Set<Coord4D> occupied;

        ConwayGrid(List<String> lines) {
            CharGrid gridZ0 = new CharGrid(lines);
            occupied = gridZ0.findCells('#').stream().map(c2 -> new Coord4D(c2.getX(), c2.getY(), 0, 0)).collect(Collectors.toSet());
        }

        ConwayGrid iterate() {
            Set<Coord4D> possibleOccupied = new HashSet<>(occupied);
            occupied.forEach(c3 -> possibleOccupied.addAll(selfAndAllNeighbours(c3)));
            Set<Coord4D> newOccupied = possibleOccupied.stream().filter(c3 -> isActive(c3, occupied)).collect(Collectors.toSet());
            return new ConwayGrid(newOccupied);
        }

        private boolean isActive(Coord4D c4, Set<Coord4D> active) {
            boolean wasActive = active.contains(c4);
            long activeNeighbours = active.stream().filter(c -> c.isAdjacentTo(c4)).count();

            return (wasActive && (activeNeighbours == 2 || activeNeighbours == 3))
                    || (activeNeighbours == 3);
        }

        private Set<Coord4D> selfAndAllNeighbours(Coord4D c) {
            Set<Coord4D> result = new HashSet<>();
            for (int x = c.getX() - 1; x <= c.getX() + 1; x++) {
                for (int y = c.getY() - 1; y <= c.getY() + 1; y++) {
                    for (int z = c.getZ() - 1; z <= c.getZ() + 1; z++) {
                        for (int w = c.getW() - 1; w <= c.getW() + 1; w++) {
                            result.add(new Coord4D(x, y, z, w));
                        }
                    }
                }
            }
            return result;
        }

        public long part2() {
            return occupied.size();
        }
    }
}
