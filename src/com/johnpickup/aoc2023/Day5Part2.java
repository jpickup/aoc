package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5Part2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day5.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            List<Range> seeds = parseSeeds(lines.get(0));
            System.out.println(seeds);

            Map<Key, List<RangeMap>> maps = new HashMap<>();

            List<String> mapLines = new ArrayList<>();
            for (int i = 2; i <= lines.size(); i++) {
                if (i == lines.size() || lines.get(i).trim().isEmpty()) {
                    // add map
                    Key key = Key.parse(mapLines.get(0));
                    mapLines.remove(0);
                    List<RangeMap> rangeMaps = mapLines.stream().map(RangeMap::parse).collect(Collectors.toList());
                    maps.put(key, rangeMaps);
                    mapLines.clear();
                } else {
                    mapLines.add(lines.get(i).trim());
                }
            }
            System.out.println(maps);

            List<String> stages = Arrays.asList("seed", "soil", "fertilizer", "water", "light", "temperature", "humidity", "location");

            List<Range> ranges = seeds;
            for (int i = 0; i < stages.size() - 1; i++) {
                ranges = applyMap(maps, stages.get(i), stages.get(i + 1), ranges);
            }

            Long minLocation = ranges.stream().map(r -> r.start).min(Long::compare).orElse(0L);
            System.out.println("Part 2 : " + minLocation);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "(ms)");
    }

    private static List<Range> applyMap(Map<Key, List<RangeMap>> maps, String from, String to, List<Range> ranges) {
        List<RangeMap> mapRangeMaps = maps.get(new Key(from, to));
        // map each input range into one or more output ranges
        return ranges.stream().sorted().flatMap(r -> applyMapsToRange(r, mapRangeMaps).stream()).collect(Collectors.toList());
    }

    private static List<Range> applyMapsToRange(Range range, List<RangeMap> rangeMaps) {
        List<Range> result = new ArrayList<>();
        long current = range.start;

        while (current < range.end()) {
            boolean madeProgress = false;
            for (RangeMap rangeMap : rangeMaps) {
                if (rangeMap.covers(current)) {
                    madeProgress = true;
                    long next = Long.min(range.end(), rangeMap.end());
                    long length = next - current;
                    result.add(new Range(rangeMap.map(current), length));
                    current = next;
                }
            }
            if (!madeProgress) {
                result.add(new Range(current, range.end() - current));
                current = range.end();
            }
        }

        return result;
    }

    private static List<Range> parseSeeds(String s) {
        List<Long> parts = Arrays.stream(s.replace("seeds: ", "").split(" ")).map(Long::parseLong).collect(Collectors.toList());
        List<Range> result = new ArrayList<>();
        for (int i = 0; i < parts.size() / 2; i++) {
            result.add(new Range(parts.get(i * 2), parts.get(i * 2 + 1)));
        }
        return result;
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
    static class Range implements Comparable<Range> {
        final long start;
        final long range;

        @Override
        public int compareTo(Range o) {
            int compare = Long.compare(this.start, o.start);
            return compare == 0 ? Long.compare(this.range, o.range) : compare;
        }

        public boolean covers(long value) {
            return (value >= start) && (value < end());
        }

        public long end() {
            return start + range;
        }
    }

    @ToString
    static class RangeMap extends Range {
        final long destination;

        public RangeMap(long destination, long start, long range) {
            super(start, range);
            this.destination = destination;
        }

        public static RangeMap parse(String s) {
            String[] parts = s.split(" ");
            return new RangeMap(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        }

        public long map(long value) {
            return covers(value) ? (value - start + destination) : value;
        }

    }
}
