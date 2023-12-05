package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day5.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            List<Long> seeds = parseSeeds(lines.get(0));
            System.out.println(seeds);

            Map<Key, List<Range>> maps = new HashMap<>();

            List<String> mapLines = new ArrayList<>();
            for (int i = 2; i <= lines.size(); i++) {
                if (i==lines.size() || lines.get(i).trim().isEmpty()) {
                    // add map
                    Key key = Key.parse(mapLines.get(0));
                    mapLines.remove(0);
                    List<Range> ranges = mapLines.stream().map(Range::parse).collect(Collectors.toList());
                    maps.put(key, ranges);
                    mapLines.clear();
                } else {
                    mapLines.add(lines.get(i).trim());
                }
            }
            System.out.println(maps);

            List<String> stages = Arrays.asList("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location");

            List<Long> locations = new ArrayList<>();

            for (Long seed : seeds) {
                long value = seed;
                System.out.println(String.format("Seed %d", value));
                for (int i = 0; i < stages.size()-1; i++) {
                    value = applyMap(maps, stages.get(i), stages.get(i+1), value);
                    System.out.println(String.format("%s -> %s = %d", stages.get(i), stages.get(i+1), value));
                }
                locations.add(value);
            }

            Long minLocation = locations.stream().min(Long::compare).orElse(0L);
            System.out.println("Part 1 : " + minLocation);


        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "(ms)");
    }

    private static long applyMap(Map<Key, List<Range>> maps, String from, String to, long value) {
        List<Range> ranges = maps.get(new Key(from, to));
        for (Range range : ranges) {
            if (range.maps(value)) {
                return range.map(value);
            }
        }
        return value;
    }

    private static List<Long> parseSeeds(String s) {
        return Arrays.stream(s.replace("seeds: ","").split(" "))
                .map(String::trim)
                .filter(z -> !z.isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    @Data
    static class Key {
        final String from;
        final String to;

        public static Key parse(String s) {
            String[] parts = s.replace(" map:", "").split("-");
            return new Key(parts[0], parts[2]);
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Range {
        final long destination;
        final long start;
        final long range;

        public static Range parse(String s) {
            String[] parts = s.split(" ");
            return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        }

        public boolean maps(long value) {
            return (value >= start) && (value < start + range);
        }

        public long map(long value) {
            return (value - start) + destination;
        }
    }

}
