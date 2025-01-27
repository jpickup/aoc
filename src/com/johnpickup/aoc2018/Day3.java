package com.johnpickup.aoc2018;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Rect;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day3 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Users/john/Development/AdventOfCode/resources/2018/" + day + "/" + day;
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
                List<Claim> claims = stream
                        .filter(s -> !s.isEmpty())
                        .map(Claim::new)
                        .collect(Collectors.toList());

                Set<Coord> part1 = new HashSet<>();
                Set<Integer> overlapping = new HashSet<>();
                for (int i = 0 ; i < claims.size(); i++) {
                    for (int j = i+1 ; j < claims.size(); j++) {
                        Claim claim1 = claims.get(i);
                        Claim claim2 = claims.get(j);
                        Set<Coord> overlappingSquares = claim1.overlappingSquares(claim2);
                        part1.addAll(overlappingSquares);
                        if (!overlappingSquares.isEmpty()) {
                            overlapping.add(claim1.id);
                            overlapping.add(claim2.id);
                        }
                    }
                }
                System.out.println("Part 1: " + part1.size());

                Set<Integer> allIds = claims.stream().map(c -> c.id).collect(Collectors.toSet());
                allIds.removeAll(overlapping);

                System.out.println("Part 2: " + allIds);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @Data
    static class Claim {
        // #1 @ 1,3: 4x4
        final int id;
        final Rect<Integer> bounds;
        static final Pattern pattern = Pattern.compile("#([0-9]+) @ ([0-9]+),([0-9]+): ([0-9]+)x([0-9]+)");
        Claim(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException(line + " does not match");
            id = Integer.parseInt(matcher.group(1));
            int left = Integer.parseInt(matcher.group(2));
            int top = Integer.parseInt(matcher.group(3));
            int width = Integer.parseInt(matcher.group(4));
            int height = Integer.parseInt(matcher.group(5));
            bounds = new Rect<>(left, top, left + width, top + height);
        }

        public Set<Coord> overlappingSquares(Claim other) {
            Rect<Integer> intersection = bounds.intersection(other.bounds);
            if (intersection != null) {
                Set<Coord> result = new HashSet<>();
                for (int x = intersection.left(); x < intersection.right(); x++) {
                    for (int y = intersection.top(); y < intersection.bottom(); y++) {
                        result.add(new Coord(x,y));
                    }
                }
                return result;
            }
            return Collections.emptySet();
        }
    }
}
