package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.CharGrid;
import com.johnpickup.aoc2024.util.Coord;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
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
                        .collect(Collectors.toList());

                List<LockKey> locks = new ArrayList<>();
                List<LockKey> keys = new ArrayList<>();

                List<String> group = new ArrayList<>();
                for (String line : lines) {
                    if (line.isEmpty()) {
                        processGroup(group, locks, keys);
                    } else {
                        group.add(line);
                    }
                }
                processGroup(group, locks, keys);

                long part1 = 0L;
                for (LockKey lock : locks) {
                    for (LockKey key : keys) {
                        if (lock.matches(key)) part1++;
                    }
                }

                System.out.println("Part 1: " + part1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static void processGroup(List<String> group, List<LockKey> locks, List<LockKey> keys) {
        LockKey lockKey = new LockKey(group);
        if (group.get(0).equals(".....")) {
            keys.add(lockKey);
        } else {
            locks.add(lockKey);
        }
        group.clear();
    }

    @ToString
    static class LockKey {
        final CharGrid grid;
        final List<Integer> heights = new ArrayList<>();
        LockKey(List<String> lines) {
            grid = new CharGrid(lines);
            for (int x = 0; x < grid.getWidth(); x++) {
                int height = 0;
                for (int y = 0; y < grid.getHeight(); y++) {
                    if (grid.getCell(new Coord(x, y)) == '#') height++;
                }
                heights.add(height-1);
            }
        }

        public boolean matches(LockKey other) {
            boolean result = true;
            for (int i = 0; i < heights.size(); i++)
                result &= heights.get(i) + other.heights.get(i) <= 5;
            return result;
        }

        @Override
        public String toString() {
            return heights.toString();
        }
    }

}
