package com.johnpickup.aoc2019;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day10 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
//                prefix + "-test.txt"
//                , prefix + "-test2.txt"
//                , prefix + "-test3.txt"
//                , prefix + "-test4.txt"
                prefix + "-test5.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                AsteroidField asteroidField = new AsteroidField(lines);

                long part1 = asteroidField.largestDetectable();
                System.out.println("Part 1: " + part1);

                List<Coord> vaporized = asteroidField.vaporized();
                if (vaporized.size() >= 200) {
                    Coord twoHundredth = vaporized.get(199);
                    long part2 = twoHundredth.getX() * 100L + twoHundredth.getY();
                    System.out.println("Part 2: " + part2);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static class AsteroidField {
        final CharGrid grid;
        AsteroidField(List<String> lines) {
            grid = new CharGrid(lines);
        }

        Coord best = null;
        public long largestDetectable() {
            long result = 0L;
            Set<Coord> asteroids = grid.findCells('#');
            for (Coord asteroid : asteroids) {
                long detectable = detectableFrom(asteroid, asteroids);
                if (detectable > result) {
                    best = asteroid;
                    result = detectable;
                }
            }
            System.out.println("Best: " + best);
            return result;
        }

        private long detectableFrom(Coord asteroid, Set<Coord> asteroids) {
            Map<Coord, Coord> deltas = asteroids.stream().collect(Collectors.toMap(a -> a, asteroid::calcDelta));
            long result = 0;
            for (Coord other : asteroids) {
                if (!asteroid.equals(other) && isDetectable(asteroid, other, deltas)) result++;
            }
            return result;
        }

        private boolean isDetectable(Coord asteroid, Coord otherAsteroid, Map<Coord, Coord> deltas) {
            boolean result = true;
            Coord delta = deltas.get(otherAsteroid);
            for (Map.Entry<Coord, Coord> entry : deltas.entrySet()) {
                if (entry.getKey().equals(asteroid) || entry.getKey().equals(otherAsteroid)) continue;
                result &= !equivalent(delta, entry.getValue());
            }
            return result;
        }

        private boolean equivalent(Coord delta, Coord other) {
            double angle1 = Math.atan2(delta.getY(), delta.getX());
            double angle2 = Math.atan2(other.getY(), other.getX());
            boolean equalRatio = Math.abs(angle1 - angle2) < 1E-6;
            boolean closer = delta.distanceFrom(Coord.ORIGIN) < other.distanceFrom(Coord.ORIGIN);
            return !closer && equalRatio;
        }

        public List<Coord> vaporized() {
            Set<Coord> asteroids = grid.findCells('#');

            Map<Coord, Double> angles = asteroids.stream()
                    .filter(a -> !a.equals(best))
                    .collect(Collectors.toMap(a -> a, a -> calcAngle(a, best)));

            Map<Double, List<Coord>> asteroidsByAngle = new TreeMap<>();
            for (Map.Entry<Coord, Double> angleEntry : angles.entrySet()) {
                asteroidsByAngle.putIfAbsent(angleEntry.getValue(), new ArrayList<>());
                asteroidsByAngle.get(angleEntry.getValue()).add(angleEntry.getKey());
            }

            List<Coord> result = new ArrayList<>();

            int count=0;

            while (anyLeft(asteroidsByAngle)) {
                for (Map.Entry<Double, List<Coord>> asteroidsForAngle : asteroidsByAngle.entrySet()) {
                    if (asteroidsForAngle.getValue().size() > 0) {
                        Coord closest = closestTo(best, asteroidsForAngle.getValue());
                        result.add(closest);
                        asteroidsForAngle.getValue().remove(closest);
                        count++;
                        System.out.printf("%d -> %s %n", count, closest);
                    }
                }
            }
            return result;
        }

        private double calcAngle(Coord a, Coord origin) {
            double angle = Math.atan2(a.getY() - origin.getY(), a.getX() - origin.getX()) + Math.PI / 2;
            if (angle < 0) angle += Math.PI * 2;
            return angle;
        }

        private Coord closestTo(Coord coord, List<Coord> coords) {
            int closestDist = Integer.MAX_VALUE;
            Coord result = null;
            for (Coord other : coords) {
                if (other.distanceFrom(coord) < closestDist) {
                    closestDist = other.distanceFrom(coord);
                    result = other;
                }
            }
            return result;
        }

        private boolean anyLeft(Map<Double, List<Coord>> asteroidsByAngle) {
            return asteroidsByAngle.values().stream().map(List::size).reduce(0, Integer::sum) > 0;
        }
    }
}
