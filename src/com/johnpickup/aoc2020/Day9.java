package com.johnpickup.aoc2020;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day9/Day9.txt"))) {
            List<Long> numbers = stream.filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());
            Encoder encoder = new Encoder(numbers, 25);
            long part1 = encoder.part1();
            System.out.println("Part 1: " + part1);
            long part2 = encoder.part2(part1);
            System.out.println("Part 2: " + part2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    static class Encoder {
        final List<Long> values;
        final int preambleLength;

        public long part1() {
            for (int i = preambleLength; i < values.size(); i++) {
                if (!hasMatchingPair(values.get(i), values.subList(i - preambleLength, i))) {
                    return values.get(i);
                }
            }
            throw new RuntimeException("Not found");
        }

        private boolean hasMatchingPair(Long target, List<Long> values) {
            for (int i = 0; i < values.size(); i++)
                for (int j = 0; j < values.size(); j++)
                    if (i != j && !values.get(i).equals(values.get(j))) {
                        if (values.get(i) +values.get(j) == target) {
                            return true;
                        }
                    }
            return false;
        }

        public long part2(long target) {
            System.out.println("Looking for " + target);
            for (int i = 0; i < values.size(); i++) {
                long sequenceTotal = 0L;
                for (int j = i; j < values.size(); j++) {
                    sequenceTotal += values.get(j);
                    if (sequenceTotal == target) {
                        List<Long> components = values.subList(i, j + 1);
                        Long min = components.stream().min(Long::compareTo).get();
                        Long max = components.stream().max(Long::compareTo).get();
                        return min + max;
                    }
                }
            }
            throw new RuntimeException("Not found");
        }
    }
}
