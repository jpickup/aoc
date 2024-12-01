package com.johnpickup.aoc2024;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day1-test.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            List<Long> left = new ArrayList<>();
            List<Long> right = new ArrayList<>();

            for (String line : lines) {
                String[] parts = line.split(" +");
                if (parts.length == 2) {
                    left.add((long) Integer.parseInt(parts[0]));
                    right.add((long) Integer.parseInt(parts[1]));
                }
            }

            left.sort(Long::compare);
            right.sort(Long::compare);

            long part1 = 0;
            for (int i = 0; i < left.size(); i++) {
                part1 += Math.abs(left.get(i) - right.get(i));
            }
            System.out.println("Part 1: " + part1);

            Map<Long, Long> rightFreq = new HashMap<>();

            for (int i = 0; i < left.size(); i++) {
                rightFreq.put(right.get(i), rightFreq.getOrDefault(right.get(i), 0L) + 1L);
            }

            long part2 = 0;
            for (long leftEntry : left) {
                Long c2 = rightFreq.getOrDefault(leftEntry, 0L);
                part2 += leftEntry * c2;
            }
            System.out.println("Part 2: " + part2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

}
