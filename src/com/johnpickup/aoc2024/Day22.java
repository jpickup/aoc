package com.johnpickup.aoc2024;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test2.txt"
                ,prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<PseudoRandomNumberGenerator> pseudoRandomNumberGenerators = stream
                        .filter(s -> !s.isEmpty())
                        .map(Long::new)
                        .map(PseudoRandomNumberGenerator::new)
                        .collect(Collectors.toList());

                long part1 = 0L;
                for (PseudoRandomNumberGenerator pseudoRandomNumberGenerator : pseudoRandomNumberGenerators) {
                    pseudoRandomNumberGenerator.runIterations(2000);
                    part1 += pseudoRandomNumberGenerator.currentValue;
                }

                System.out.println("Part 1: " + part1);     // 17163502021

                Set<List<Long>> allSubsequences = new HashSet<>();
                for (PseudoRandomNumberGenerator pseudoRandomNumberGenerator : pseudoRandomNumberGenerators) {
                    allSubsequences.addAll(pseudoRandomNumberGenerator.allSubsequences);
                }

                long bestEarning = 0L;
                List<Long> bestSequence = null;
                for (List<Long> subsequence : allSubsequences) {
                    long earning = 0L;
                    for (PseudoRandomNumberGenerator pseudoRandomNumberGenerator : pseudoRandomNumberGenerators) {
                        earning += pseudoRandomNumberGenerator.calculateEarning(subsequence);
                    }
                    if (earning > bestEarning) {
                        bestEarning = earning;
                        bestSequence = subsequence;
                    }
                }
                System.out.println("Part 2: " + bestEarning);
                System.out.println(bestSequence);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class PseudoRandomNumberGenerator {
        private final long seed;
        private Long currentValue;

        public PseudoRandomNumberGenerator(long seed) {
            this.seed = seed;
            this.currentValue = seed;
        }

        long next() {
            currentValue = generate(Optional.ofNullable(currentValue).orElse(seed));
            return currentValue;
        }

        long price() {
            return currentValue % 10;
        }

        private long generate(long v) {
            long step1 = prune(mix(v * 64, v));
            long step2 = prune(mix(step1 / 32, step1));
            long step3 = prune(mix(step2 * 2048, step2));
            return step3;
        }

        private long mix(long v1, long v2) {
            return v1 ^ v2;
        }

        private long prune(long v) {
            return v % 16777216;
        }

        List<Long> prices = new ArrayList<>();
        List<Long> differences = new ArrayList<>();
        Map<List<Long>, Long> firstPriceForSequence = new HashMap<>();
        Set<List<Long>> allSubsequences = new HashSet<>();

        public void runIterations(int iterations) {
            long prev = price();
            prices.add(prev);
            for (int i = 0; i < iterations; i++) {
                next();
                long price = price();
                prices.add(price);
                differences.add(price - prev);
                prev = price;
            }

            for (int i = 0; i < prices.size()-4; i++) {
                Long price = prices.get(i+4);
                List<Long> lastFourDiffs = differences.subList(i, i+4);
                allSubsequences.add(lastFourDiffs);
                firstPriceForSequence.putIfAbsent(lastFourDiffs, price);
            }
        }

        public long calculateEarning(List<Long> sequence) {
            return Optional.ofNullable(this.firstPriceForSequence.get(sequence)).orElse(0L);
        }
    }
}
