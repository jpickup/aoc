package com.johnpickup.aoc2023;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day1.txt"))) {
            long start = System.currentTimeMillis();
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            long result = 0;

            for (String line : lines) {
                Character first = null;
                Character last = null;

                for (int i = 0; i < line.length(); i++) {
                    String d = initialDigit(line.substring(i));
                    char digit = d.isEmpty()?line.charAt(i): valueOf(d).charAt(0);
                    if (Character.isDigit(digit)) {
                        if (first == null) {
                            first = digit;
                        }
                        last = digit;
                    }
                }
                if (first != null) {
                    long value = (first - '0') * 10 + last - '0';
                    System.out.println("Value: " + value + " from " + line);
                    result += value;
                }
            }

            System.out.println("Result: " + result);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String initialDigit(String s) {
        if (s.startsWith("one")) return "one";
        if (s.startsWith("two")) return "two";
        if (s.startsWith("three")) return "three";
        if (s.startsWith("four")) return "four";
        if (s.startsWith("five")) return "five";
        if (s.startsWith("six")) return "six";
        if (s.startsWith("seven")) return "seven";
        if (s.startsWith("eight")) return "eight";
        if (s.startsWith("nine")) return "nine";
        return "";
    }

    private static final Map<String, String> replacements = new HashMap<>();
    static {
        replacements.put("one","1");
        replacements.put("two","2");
        replacements.put("three","3");
        replacements.put("four","4");
        replacements.put("five","5");
        replacements.put("six","6");
        replacements.put("seven","7");
        replacements.put("eight","8");
        replacements.put("nine","9");
    }

    private static String valueOf(String s) {
        return replacements.getOrDefault(s, s);
    }
}
