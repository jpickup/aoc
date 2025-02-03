package com.johnpickup.aoc2020;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord3D;
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



public class Day17 {
    static boolean isTest;

    public static void main(String[] args) {
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
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

                System.out.println("Part 1: " + conwayGrid.occupiedCount());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class ConwayGrid {
        final Set<Coord3D> occupied;

        ConwayGrid(List<String> lines) {
            CharGrid gridZ0 = new CharGrid(lines);
            occupied = gridZ0.findCells('#').stream().map(c2 -> new Coord3D(c2.getX(), c2.getY(), 0)).collect(Collectors.toSet());
        }

        ConwayGrid iterate() {
            Set<Coord3D> possibleOccupied = new HashSet<>(occupied);
            occupied.forEach(c3 -> possibleOccupied.addAll(selfAndAllNeighbours(c3)));
            Set<Coord3D> newOccupied = possibleOccupied.stream().filter(c3 -> isActive(c3, occupied)).collect(Collectors.toSet());
            return new ConwayGrid(newOccupied);
        }

        private boolean isActive(Coord3D c3, Set<Coord3D> active) {
            boolean wasActive = active.contains(c3);
            long activeNeighbours = active.stream().filter(c -> c.isAdjacentTo26(c3)).count();

            return (wasActive && (activeNeighbours == 2 || activeNeighbours == 3))
                    || (activeNeighbours == 3);
        }

        private Set<Coord3D> selfAndAllNeighbours(Coord3D c3) {
            Set<Coord3D> result = new HashSet<>();
            for (int x = c3.getX() - 1; x <= c3.getX() + 1; x++) {
                for (int y = c3.getY() - 1; y <= c3.getY() + 1; y++) {
                    for (int z = c3.getZ() - 1; z <= c3.getZ() + 1; z++) {
                        result.add(new Coord3D(x, y, z));
                    }
                }
            }
            return result;
        }

        public long occupiedCount() {
            return occupied.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            int minX = occupied.stream().map(Coord3D::getX).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            int maxX = occupied.stream().map(Coord3D::getX).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            int minY = occupied.stream().map(Coord3D::getY).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            int maxY = occupied.stream().map(Coord3D::getY).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            int minZ = occupied.stream().map(Coord3D::getZ).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));
            int maxZ = occupied.stream().map(Coord3D::getZ).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No cells"));

            for (int z = minZ; z <= maxZ; z++) {
                sb.append("z=" + z).append('\n');
                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        Coord3D c = new Coord3D(x, y, z);
                        sb.append(occupied.contains(c) ? '#' : '.');
                    }
                    sb.append('\n');
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }
}
