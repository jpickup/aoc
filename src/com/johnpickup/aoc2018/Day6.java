package com.johnpickup.aoc2018;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Maps;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day6 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2018/" + day + "/" + day;
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
                List<Coord> coords = stream
                        .filter(s -> !s.isEmpty())
                        .map(Coord::new)
                        .collect(Collectors.toList());

                Areas areas = new Areas(coords);
                long part1 = areas.part1();
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);
                // 135466 too high

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Areas {
        final List<Coord> points;

        public long part1() {
            int minX = points.stream().map(Coord::getX).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No points"));
            int minY = points.stream().map(Coord::getY).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No points"));
            int maxX = points.stream().map(Coord::getX).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No points"));
            int maxY = points.stream().map(Coord::getY).max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No points"));

            int width = maxX - minX;
            int height = maxY - minY;

            System.out.println(width + " * " + height);

            Map<Coord, Coord> closestTo = new HashMap<>();
            Set<Coord> infinitePoints = new HashSet<>();

            for (int x = minX - width; x < maxX + width; x++) {
                for (int y = minY - height; y < maxY + height; y++) {
                    Coord c = new Coord(x,y);
                    Map<Coord, Integer> distanceFrom = points.stream().collect(Collectors.toMap(p -> p, p -> p.distanceFrom(c)));
                    int leastDistance = distanceFrom.values().stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No least distance"));
                    List<Map.Entry<Coord, Integer>> closestPoints = distanceFrom.entrySet().stream().filter(e -> e.getValue().equals(leastDistance)).collect(Collectors.toList());
                    if (closestPoints.size() == 1) {
                        Coord closest = closestPoints.get(0).getKey();
                        closestTo.put(c, closest);
                        // anything on a grid edge is infinite
                        if (x == minX - width || x ==  maxX + width - 1 || y == minY - height || y == maxY + height - 1) {
                            infinitePoints.add(closest);
                        }
                    }
                }
            }

            Map<Coord, Set<Coord>> byClosest = Maps.reverseMap(closestTo);
            Map<Coord, Integer> byClosestCount = byClosest.entrySet().stream()
                    .filter(e -> !infinitePoints.contains(e.getKey()))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size()));
            System.out.println(byClosestCount);
            return byClosestCount.values().stream().max(Integer::compareTo).orElseThrow(() -> new RuntimeException("No max area"));
        }
    }
}
