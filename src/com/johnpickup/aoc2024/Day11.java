package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    public static void main(String[] args) {
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day11";
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Stones stones = new Stones(lines.get(0));

                long part1 = stones.part0();
                System.out.println("Part 1: " + part1);
                //long part2 = stones.part1();
                //System.out.println("Part 2: " + part2);

                // target p1: 55312 & 193269
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }
    @ToString
    @RequiredArgsConstructor
    static class Stones {
        final Map<State, Long> cache = new HashMap<>();
        final List<Long> stones;

        Stones(String line) {
            stones = Arrays.stream(line.split(" ")).map(Long::parseLong).collect(Collectors.toList());
        }

        public long part0() {
            return solve(25);
        }

        public long part1() {
            return solve(75);
        }

        long solve(int iterations) {
            return stones.stream().map(s -> solveSingle(s, iterations)).reduce(0L, Long::sum);
        }

        private long solveSingle(long stone, int iterations) {
            if (iterations == 0) return 1;
            State state = new State(stone, iterations);
            //if (cache.containsKey(state)) return cache.get(state);
            State returnState = new State(stone, iterations-1);
            if (stone == 0) return cacheResult(solveSingle(1L, iterations-1), returnState);
            if (evenDigits(stone)) {
                return cacheResult(solveSingle(leftDigits(stone), iterations-1), returnState)
                + cacheResult(solveSingle(rightDigits(stone), iterations-1), returnState);
            }
            return cacheResult(solveSingle(stone * 2024, iterations-1), returnState);
        }

        private long cacheResult(long solution, State state) {
            cache.put(state, solution);
            return solution;
        }

        private long leftDigits(long stoneValue) {
            String decimal = String.format("%d", stoneValue);
            return Long.parseLong(decimal.substring(0, (decimal.length() / 2)));
        }

        private long rightDigits(long stoneValue) {
            String decimal = String.format("%d", stoneValue);
            return Long.parseLong(decimal.substring((decimal.length() / 2)));
        }

        private boolean evenDigits(long stoneValue) {
            String decimal = String.format("%d", stoneValue);
            return decimal.length() % 2 == 0;
        }
    }

    @RequiredArgsConstructor
    @Data
    static class State {
        final long value;
        final int iterations;
    }
}
