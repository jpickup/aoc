package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Original part 1:
// 2000000 = 5607466
// Time (ms) : 2320


public class Day15 {
    static int part = 2;
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day15.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            List<Coord> sensors = lines.stream().map(Day15::parseSensor).map(Coord::parse).collect(Collectors.toList());
            List<Coord> beacons = lines.stream().map(Day15::parseBeacon).map(Coord::parse).collect(Collectors.toList());
            Map<Coord, Coord> closestBeaconBySensor = new HashMap<>();
            Map<Coord, Coord> closestSensorByBeacon = new HashMap<>();
            for (int i = 0; i < sensors.size(); i++) {
                closestBeaconBySensor.put(sensors.get(i), beacons.get(i));
                closestSensorByBeacon.put(beacons.get(i), sensors.get(i));
            }

            // get distances for each pair - as a map of beacon to distance
            Map<Coord, Integer> closestBeaconDistances = closestBeaconBySensor.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> distanceBetween(e.getKey(), e.getValue())));
            Map<Coord, Integer> closestSensorDistances = closestSensorByBeacon.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> distanceBetween(e.getKey(), e.getValue())));

            int minX = sensors.get(0).x, maxX = sensors.get(0).x, minY = sensors.get(0).y, maxY = sensors.get(0).y;
            for (Coord coord : sensors) {
                if (coord.x < minX) minX = coord.x;
                if (coord.y < minY) minY = coord.y;
                if (coord.x > maxX) maxX = coord.x;
                if (coord.y > maxY) maxY = coord.y;
            }
            for (Coord coord : beacons) {
                if (coord.x < minX) minX = coord.x;
                if (coord.y < minY) minY = coord.y;
                if (coord.x > maxX) maxX = coord.x;
                if (coord.y > maxY) maxY = coord.y;
            }

            int maxDist = closestBeaconDistances.values().stream().max(Integer::compare).get();

            minX -= maxDist;
            maxX += maxDist;
            minY -= maxDist;
            maxY += maxDist;

            long start = System.currentTimeMillis();

            if (part == 1) {
                // part1
                int y = 2000000;
                int result = calcKnown(y, closestBeaconBySensor, closestBeaconDistances, beacons, sensors, minX, maxX);
                System.out.println(y + " = " + result);
            }
            else {
                // part 2
                minX = 0;
                maxX = 4000000;
                minY = 0;
                maxY = 4000000;
                Set<Coord> intersections = calcIntersections(closestBeaconDistances, beacons, sensors, minX, maxX, minY, maxY);
                System.out.println(intersections.size());

                Set<Coord> validIntersections = intersections.stream().filter(c -> isValid(c, closestBeaconBySensor, closestBeaconDistances, beacons, sensors)).collect(Collectors.toSet());

                System.out.println(validIntersections.size());
                System.out.println(validIntersections);
                Coord theOne = validIntersections.stream().findFirst().get();

                BigInteger result = BigInteger.valueOf(theOne.x).multiply(BigInteger.valueOf(4000000)).add(BigInteger.valueOf(theOne.y));
                System.out.println(result);
            }

            long end = System.currentTimeMillis();
            System.out.println("Time (ms) : " + (end - start));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValid(Coord c, Map<Coord, Coord> closestBeaconBySensor, Map<Coord, Integer> closestDistances, List<Coord> beacons, List<Coord> sensors) {
        if (!beacons.contains(c) && !sensors.contains(c)) {
            for (Map.Entry<Coord, Coord> closestEntry : closestBeaconBySensor.entrySet()) {
                Coord sensor = closestEntry.getKey();
                Coord beacon = closestEntry.getValue();
                int closestBeaconDistanceForSensor = closestDistances.get(sensor);
                int currentDistanceFromSensor = distanceBetween(sensor, c);
                if (currentDistanceFromSensor <= closestBeaconDistanceForSensor) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Set<Coord> calcIntersections(Map<Coord, Integer> closestBeaconDistances, List<Coord> beacons, List<Coord> sensors, int minX, int maxX, int minY, int maxY) {
        Set<Coord> result = new HashSet<>();

        for (Coord sensor1 : sensors) {
            for (Coord sensor2 : sensors) {
                if (!sensor1.equals(sensor2)) {
                    int d1 = closestBeaconDistances.get(sensor1) + 1;
                    int d2 = closestBeaconDistances.get(sensor2) + 1;
                    int x1 = sensor1.x;
                    int y1 = sensor1.y;
                    int x2 = sensor2.x;
                    int y2 = sensor2.y;

                    result.add(Coord.builder()
                            .x((y2 + x2 + x1 - y1 - d2 + d1) / 2)
                            .y((y2 + x2 + x1 - y1 - d2 + d1) / 2  + (y1 - x1) - d1)
                            .build());

                    result.add(Coord.builder()
                            .x((y2 + x2 + x1 - y1 + d2 + d1) / 2)
                            .y((y2 + x2 + x1 - y1 + d2 + d1) / 2  + (y1 - x1) - d1)
                            .build());

                    result.add(Coord.builder()
                            .x((y2 + x2 + x1 - y1 - d2 - d1) / 2)
                            .y((y2 + x2 + x1 - y1 - d2 - d1) / 2  + (y1 - x1) + d1)
                            .build());

                    result.add(Coord.builder()
                            .x((y2 + x2 + x1 - y1 + d2 - d1) / 2)
                            .y((y2 + x2 + x1 - y1 + d2 - d1) / 2  + (y1 - x1) + d1)
                            .build());
                }
            }
        }
        return result.stream().filter(c -> c.x>=minX && c.x<=maxX && c.y>=minY && c.y<=maxY).collect(Collectors.toSet());
    }

    // for each coord on the line, see if closer or equal to any beacon than the mapped closest distance, if it is then it's known
    private static int calcKnown(int y, Map<Coord, Coord> closestBeaconBySensor, Map<Coord, Integer> closestDistances, List<Coord> beacons, List<Coord> sensors, int minX, int maxX) {
        int x = minX;
        int result = 0;

        while (x <= maxX) {
            Coord current = Coord.builder().x(x).y(y).build();
            if (!beacons.contains(current) && !sensors.contains(current)) {
                for (Map.Entry<Coord, Coord> closestEntry : closestBeaconBySensor.entrySet()) {
                    Coord sensor = closestEntry.getKey();
                    Coord beacon = closestEntry.getValue();
                    int closestBeaconDistanceForSensor = closestDistances.get(sensor);
                    int currentDistanceFromSensor = distanceBetween(sensor, current);
                    if (currentDistanceFromSensor <= closestBeaconDistanceForSensor) {
                        //System.out.println(current + " is known as it is " + currentDistanceFromSensor + " away from " + sensor + " which is as close as " + beacon + " (" + closestBeaconDistanceForSensor + ")");
                        result++;
                        break;
                    }
                }
                //System.out.println(x + " - " + result);
            }
            x++;
        }
        return result;
    }

    private static int distanceBetween(Coord c1, Coord c2) {
        return Math.abs(c1.x - c2.x) + Math.abs(c1.y - c2.y);
    }

    private static String parseSensor(String s) {
        int start = s.indexOf("at ") + 3;
        int end = s.indexOf(":");
        return s.substring(start, end);
    }

    private static String parseBeacon(String s) {
        int start = s.indexOf("at ", s.indexOf(":")) + 3;
        return s.substring(start);
    }

    @Builder
    @EqualsAndHashCode
    static class Coord {
        final int x;
        final int y;

        static Coord parse(String s) {
            String[] parts = s.trim().split(",");
            return Coord.builder()
                    .x(Integer.parseInt(parts[0].substring(2)))
                    .y(Integer.parseInt(parts[1].substring(3)))
                    .build();
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }
}
