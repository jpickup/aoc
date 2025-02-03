package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day19 {
    static final int MAX_TIME = 24;

    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day19-test.txt"))) {
            long start = System.currentTimeMillis();
            List<Blueprint> blueprints = stream.filter(s -> !s.isEmpty()).map(Blueprint::parse).collect(Collectors.toList());

            Map<Blueprint, Integer> scores = blueprints.stream().collect(Collectors.toMap(b -> b, b -> b.maxGeodes() * b.id));
            Integer totalScore = scores.values().stream().reduce(0, Integer::sum);

            System.out.println("PART 1 - Total Score: " + totalScore);
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Builder
    @EqualsAndHashCode
    @ToString
    static class Blueprint {
        final int id;
        final Map<Resource, Robot> robots;
        static Blueprint parse(String s) {
            // Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
            String[] parts = s.split(":");
            int id = Integer.parseInt(parts[0].substring(10));
            String[] robotParts = parts[1].split("\\.");

            List<Robot> robots = Arrays.stream(robotParts).map(String::trim).map(Robot::parse).collect(Collectors.toList());

            return Blueprint.builder()
                    .id(id)
                    .robots(robots.stream().collect(Collectors.toMap(r -> r.produces, r -> r)))
                    .build();
        }

        int maxGeodes() {
            return 0;
        }
    }

    @Builder
    @EqualsAndHashCode
    @ToString
    static class Robot {
        Resource produces;
        Map<Resource, Integer> costs;
        public static Robot parse(String s) {
            // Each obsidian robot costs 3 ore and 14 clay
            String[] words = s.split(" ");

            Map<Resource, Integer> costs = new HashMap<>();
            for (int i = 4; i < words.length; i+=3) {
                int count = Integer.parseInt(words[i]);
                Resource resource = Resource.valueOf(words[i + 1].toUpperCase());
                costs.put(resource, count);
            }

            return Robot.builder()
                    .produces(Resource.valueOf(words[1].toUpperCase()))
                    .costs(costs)
                    .build();
        }
    }

    enum Resource {
        ORE,
        CLAY,
        OBSIDIAN,
        GEODE
    }
}
