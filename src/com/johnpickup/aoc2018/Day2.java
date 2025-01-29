package com.johnpickup.aoc2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;
import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day2 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                long twos = lines.stream().filter(s -> hasDuplicates(s,2)).count();
                long threes = lines.stream().filter(s -> hasDuplicates(s,3)).count();
                System.out.println("Part 1: " + twos * threes);
                String part2 = findSingleCharDifference(lines);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static String findSingleCharDifference(List<String> strings) {
        for (String s1 : strings) {
            for (String s2 : strings) {
                if (!s1.equals(s2)) {
                    if (diffCount(s1, s2) == 1) {
                        return common(s1, s2);
                    }
                }
            }
        }
        return null;
    }

    private static String common(String s1, String s2) {
        if (s1.length() != s2.length()) throw new RuntimeException("Not same length");
        String result = "";
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == s2.charAt(i)) result += s1.charAt(i);
        }
        return result;
    }

    private static int diffCount(String s1, String s2) {
        if (s1.length() != s2.length()) throw new RuntimeException("Not same length");
        int result = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) result ++;
        }
        return result;
    }

    private static boolean hasDuplicates(String s, int len) {
        Map<Character, Integer> charFreq = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            charFreq.put(s.charAt(i),charFreq.getOrDefault(s.charAt(i),0) + 1);
        }
        return charFreq.values().stream().filter(v -> v.equals(len)).count() > 0;
    }

}
