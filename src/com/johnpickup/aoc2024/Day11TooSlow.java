package com.johnpickup.aoc2024;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11TooSlow {
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
                long part2 = stones.part1();
                System.out.println("Part 2: " + part2);

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
        final List<Long> stones;

        Stones(String line) {
            stones = Arrays.stream(line.split(" ")).map(Long::parseLong).collect(Collectors.toList());
        }

        public long part0() {
            Stones s = this;
            for (int i = 0; i < 25; i++) {
                s = s.blink();
            }
            return s.size();
        }

        public long part1() {
            Stones s = this;
            for (int i = 0; i < 75; i++) {
                s = s.blink();
            }
            return s.size();
        }

        int size() {
            return stones.size();
        }

        Stones blink() {
            List<Long> newStones = new ArrayList<>();
            for (int i = 0; i < stones.size(); i++) {
                long stoneValue = stones.get(i);
                if (stoneValue==0L) {
                    newStones.add(1L);
                }
                else if (evenDigits(stoneValue)) {
                    newStones.add(leftDigits(stoneValue));
                    newStones.add(rightDigits(stoneValue));
                }
                else {
                    newStones.add(stoneValue * 2024);
                }
            }
            return new Stones(newStones);
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
}
