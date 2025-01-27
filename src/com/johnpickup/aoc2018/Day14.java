package com.johnpickup.aoc2018;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day14 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Users/john/Development/AdventOfCode/resources/2018/Day14/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + "-test3.txt"
                , prefix + "-test4.txt"
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

                String part1 = Recipe.part1(Recipe.generate(37), Integer.parseInt(lines.get(0)));
                System.out.println("Part 1: " + part1);

                long part2 = Recipe.part2(Recipe.generate(37), lines.get(1));
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @Data
    static class Recipe {
        static String part1(String initial, int target) {
            int elf1 = 0;
            int elf2 = 1;
            StringBuilder recipes = new StringBuilder(initial);
            while (recipes.length() < target + 11) {
                String newRecipes = combine(recipes.charAt(elf1), recipes.charAt(elf2));
                recipes.append(newRecipes);
                elf1 = (1 + elf1 + recipes.charAt(elf1)-'0') % recipes.length();
                elf2 = (1 + elf2 + recipes.charAt(elf2)-'0') % recipes.length();
            }
            return recipes.substring(target, target + 10);
        }

        static long part2(String initial, String target) {
            int elf1 = 0;
            int elf2 = 1;
            StringBuilder recipes = new StringBuilder(initial);
            while (recipes.length() < target.length() || !lastPart(recipes, target.length()+1).contains(target)) {
                String newRecipes = combine(recipes.charAt(elf1), recipes.charAt(elf2));
                recipes.append(newRecipes);
                elf1 = (1 + elf1 + recipes.charAt(elf1)-'0') % recipes.length();
                elf2 = (1 + elf2 + recipes.charAt(elf2)-'0') % recipes.length();
            }
            return recipes.indexOf(target);
        }

        static String lastPart(StringBuilder sb, int length) {
            if (sb.length() <= length) return sb.toString();
            StringBuilder result = new StringBuilder();
            for (int i = sb.length()-length; i<sb.length(); i++) {
                result.append(sb.charAt(i));
            }
            return result.toString();
        }
        static String combine(char r1, char r2) {
            return generate((r1 - '0') + (r2 - '0'));
        }

        public static String generate(long totalScore) {
            return Long.toString(totalScore);
        }
    }
}
