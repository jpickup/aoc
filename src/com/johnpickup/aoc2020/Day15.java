package com.johnpickup.aoc2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day15 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
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
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                MemoryGame memoryGame = new MemoryGame(lines.get(0));
                long part1 = memoryGame.part1();
                System.out.println("Part 1: " + part1);
                memoryGame.reset();
                long part2 = memoryGame.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class MemoryGame {
        final List<Integer> initialNumbers;
        final Map<Long, Long> turnLastUsed = new HashMap<>();
        final Map<Long, Long> secondLastUsed = new HashMap<>();
        MemoryGame(String line) {
            initialNumbers = Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        }

        void reset() {
            secondLastUsed.clear();
            turnLastUsed.clear();
        }

        long part1() {
            return playGame(2020);
        }

        long part2() {
            return playGame(30000000);
        }

        private long playGame(int turnCount) {
            long i;
            long lastNumber = 0;
            boolean wasNew = true;
            long number = 0L;
            for (i = 1; i <= initialNumbers.size(); i++) {
                number = initialNumbers.get((int)(i-1));
                wasNew = !turnLastUsed.containsKey(number);
                secondLastUsed.put(number,turnLastUsed.getOrDefault(number,0L));
                turnLastUsed.put(number, i);
                lastNumber = number;
            }

            while (i <= turnCount) {
                if (wasNew) number = 0;
                else number = turnLastUsed.get(lastNumber) - secondLastUsed.get(lastNumber);
                wasNew = !turnLastUsed.containsKey(number);
                secondLastUsed.put(number,turnLastUsed.getOrDefault(number,0L));
                turnLastUsed.put(number, i);
                lastNumber = number;
                i++;
            }
            return number;
        }
    }
}
