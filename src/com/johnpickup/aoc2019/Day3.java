package com.johnpickup.aoc2019;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Direction;
import com.johnpickup.util.Maps;
import com.johnpickup.util.Sets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day3 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
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
                List<Path> paths = stream
                        .filter(s -> !s.isEmpty())
                        .map(Path::new)
                        .collect(Collectors.toList());

                Path wire1 = paths.get(0);
                Path wire2 = paths.get(1);

                Set<Coord> wire1Points = wire1.points();
                Set<Coord> wire2Points = wire2.points();

                Set<Coord> intersections = Sets.intersection(wire1Points, wire2Points);
                System.out.println(intersections);

                int part1 = intersections.stream().map(i -> i.distanceFrom(Coord.ORIGIN)).min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No intersections"));
                System.out.println("Part 1: " + part1);

                Map<Integer, Coord> wire1StepPoints = wire1.stepPoints();
                Map<Integer, Coord> wire2StepPoints = wire2.stepPoints();

                Map<Coord, Set<Integer>> wire1PointSteps = Maps.reverseMap(wire1StepPoints);
                Map<Coord, Set<Integer>> wire2PointSteps = Maps.reverseMap(wire2StepPoints);

                int part2 = Integer.MAX_VALUE;
                for (Coord intersection : intersections) {
                    int stepsToIntersectionOnWire1 = wire1PointSteps.get(intersection).stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No intersection at " + intersection));
                    int stepsToIntersectionOnWire2 = wire2PointSteps.get(intersection).stream().min(Integer::compareTo).orElseThrow(() -> new RuntimeException("No intersection at " + intersection));
                    int steps = stepsToIntersectionOnWire1 + stepsToIntersectionOnWire2;
                    if (steps < part2) part2 = steps;
                }
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Path {
        final List<Step> steps;
        Path(String line) {
            steps = Arrays.stream(line.split(",")).map(Step::new).collect(Collectors.toList());
        }

        Set<Coord> points() {
            return new HashSet<>(stepPoints().values());
        }

        Map<Integer, Coord> stepPoints() {
            Map<Integer, Coord> result = new HashMap<>();
            Coord location = Coord.ORIGIN;
            int s = 0;
            for (Step step : steps) {
                for (int i=0; i < step.distance; i++) {
                    location = location.move(step.direction, 1);
                    result.put(++s, location);
                }
            }
            return result;
        }
    }

    static class Step {
        final Direction direction;
        final int distance;

        Step(String s) {
            distance = Integer.parseInt(s.substring(1));
            switch (s.charAt(0)) {
                case 'U': direction = Direction.NORTH; break;
                case 'D': direction = Direction.SOUTH; break;
                case 'L': direction = Direction.WEST; break;
                case 'R': direction = Direction.EAST; break;
                default: throw new RuntimeException("Unknown direction " + s);
            }
        }
    }

}
