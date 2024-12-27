package com.johnpickup.aoc2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day6/Day6.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            List<String> prevLines = new ArrayList<>();
            List<DeclarationForm> forms = new ArrayList<>();
            for (String line : lines) {
                if (line.isEmpty()) {
                    forms.add(new DeclarationForm(prevLines));
                    prevLines.clear();
                } else {
                    prevLines.add(line);
                }
            }

            int part1 = forms.stream().map(DeclarationForm::part1).reduce(0, Integer::sum);
            System.out.println("Part 1: " + part1);
            int part2 = forms.stream().map(DeclarationForm::part2).reduce(0, Integer::sum);
            System.out.println("Part 2: " + part2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    static class DeclarationForm {
        final Set<Character> answers = new HashSet<>();
        final Set<Character> commonAnswers = new HashSet<>();
        DeclarationForm(List<String> lines) {
            for (char c = 'a'; c <= 'z'; c++) {
                commonAnswers.add(c);
            }

            for (String line : lines) {
                for (int i = 0; i < line.length(); i++) {
                    answers.add(line.charAt(i));
                }

                for (char c = 'a'; c <= 'z'; c++) {
                    if (!line.contains(c + "")) {
                        commonAnswers.remove(c);
                    }
                }
            }
        }

        int part1() {
            return answers.size();
        }

        public int part2() {
            System.out.println(commonAnswers);
            return commonAnswers.size();
        }
    }
}
