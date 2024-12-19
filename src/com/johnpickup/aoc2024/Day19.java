package com.johnpickup.aoc2024;

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

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day19 {
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Towels towels = new Towels(lines);
                System.out.println(towels);

                long part1 = towels.part1();
                System.out.println("Part 1: " + part1);
                long part2 = towels.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }
    @ToString
    static class Towels {
        final List<String> towels;
        final List<String> designs;

        Towels(List<String> lines) {
            towels = Arrays.asList(lines.get(0).split(", "));
            designs = lines.subList(1, lines.size());
        }

        public long part1() {
            return designs.stream().filter(this::isPossible).count();
        }

        public long part2() {
            return designs.stream().filter(this::isPossible)
                    .map(this::countPossible).reduce(0L, Long::sum);
        }

        final Map<String, Long> cachedResults = new HashMap<>();
        private long countPossible(String design) {
            if (cachedResults.containsKey(design)) return cachedResults.get(design);
            if (design.isEmpty()) return 1;
            long result = towels.stream()
                    .filter(design::startsWith)
                    .map(t -> countPossible(design.substring(t.length())))
                    .reduce(0L, Long::sum);
            cachedResults.put(design, result);
            return result;
        }

        private boolean isPossible(String design) {
            if (design.isEmpty()) return true;
            return towels.stream()
                    .filter(design::startsWith)
                    .anyMatch(t -> isPossible(design.substring(t.length())));
        }
    }
}
