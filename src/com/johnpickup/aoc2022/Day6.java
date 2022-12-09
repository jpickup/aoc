package com.johnpickup.aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day6-test.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            for (String line : lines) {
                System.out.println("P1:" + part1(line));
                System.out.println("P2:" + part2(line));
            }

            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long part1(String line) {
        for (int i=4; i < line.length(); i++) {
            if (allDifferent(line.substring(i-4,i))) return i;
        }
        return -1;
    }

    private static long part2(String line) {
        for (int i=14; i < line.length(); i++) {
            if (allDifferent(line.substring(i-14,i))) return i;
        }
        return -1;
    }

    private static boolean allDifferent(String s) {
        boolean result = true;
        for (int i = 0; i < s.length(); i++) {
            for (int j = 0; j < i; j++) {
                result = result && s.charAt(i) != s.charAt(j);
            }
        }
        return result;
    }
}
