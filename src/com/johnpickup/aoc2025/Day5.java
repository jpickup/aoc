package com.johnpickup.aoc2025;

import com.johnpickup.util.Range;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day5 {
    static boolean isTest;

    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object() {});
        for (String inputFilename : inputFilenames) {

            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .toList();

                List<Range<Long>> freshIngredients = parseRanges(lines);
                List<Long> ingredients = parseScalars(lines);

                long part1 = ingredients.stream().filter(i -> isFresh(i, freshIngredients)).count();
                System.out.println("Part 1: " + part1);

                long part2 = howManyFresh(freshIngredients);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static boolean isFresh(long ingredient, List<Range<Long>> freshIngredients) {
        return freshIngredients.stream().anyMatch(fi -> fi.containsValue(ingredient));
    }

    private static long howManyFresh(List<Range<Long>> freshIngredients) {
        Set<Range<Long>> distinctRanges = combineRanges(freshIngredients);
        return distinctRanges.stream().map(r -> r.getUpper() - r.getLower() + 1).reduce(0L, Long::sum);
    }

    private static Set<Range<Long>> combineRanges(List<Range<Long>> freshIngredients) {
        TreeSet<Range<Long>> result = new TreeSet<>();
        for (Range<Long> curr : new TreeSet<>(freshIngredients)) {
            if (result.isEmpty() || !result.last().overlapsInclusive(curr)) {
                result.add(curr);
            } else {
                Range<Long> prev = result.removeLast();
                result.add(new Range<>(Math.min(prev.getLower(), curr.getLower()), Math.max(prev.getUpper(), curr.getUpper())));
            }
        }
        return result;
    }

    private static List<Range<Long>> parseRanges(List<String> lines) {
        return lines.stream()
                .filter(l -> !l.isEmpty())
                .filter(l -> l.contains("-"))
                .map(l -> new Range<Long>(l, Long::parseLong))
                .collect(Collectors.toList());
    }

    private static List<Long> parseScalars(List<String> lines) {
        return lines.stream()
                .filter(l -> !l.isEmpty())
                .filter(l -> !l.contains("-"))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
