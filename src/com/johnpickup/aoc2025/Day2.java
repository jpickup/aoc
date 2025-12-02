package com.johnpickup.aoc2025;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day2 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Range> ranges = stream
                        .filter(s -> !s.isEmpty())
                        .map(s -> s.split(","))
                        .flatMap(a -> Arrays.stream(a).map(Range::parse))
                        .toList();

                long part1 = 0L;
                long part2 = 0L;
                for (Range range : ranges) {
                    for (long l : range.invalidValuesPart1()) {
                        part1 += l;
                    }
                    for (long l : range.invalidValuesPart2()) {
                        part2 += l;
                    }
                }
                System.out.println("Part 1: " + part1);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Range {
        final long lower;
        final long higher;

        public static Range parse(String s) {
            String[] parts = s.split("-");
            return new Range(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        }

        public List<Long> invalidValuesPart1() {
            List<Long> result = new ArrayList<>();
            for (long v = lower; v <= higher; v++) {
                if (isInvalidPart1(v)) {
                    result.add(v);
                }
            }
            return result;
        }

        public List<Long> invalidValuesPart2() {
            List<Long> result = new ArrayList<>();
            for (long v = lower; v <= higher; v++) {
                if (isInvalidPart2(v)) {
                    result.add(v);
                }
            }
            return result;
        }

        private static boolean isInvalidPart1(long l) {
            String s = Long.toString(l);
            return (s.length()%2==0) &&
                    (s.substring(0, s.length()/2).equals(s.substring(s.length()/2)));
        }

        private static boolean isInvalidPart2(long l) {
            String s = Long.toString(l);
            for (int i=0; i< s.length()/2; i++) {
                if (stringIsRepeat(s, s.substring(0, i+1))) {
                    return true;
                }
            }
            return false;
        }

        private static boolean stringIsRepeat(String s, String substring) {
            if (s.length() % substring.length() != 0) return false;
            return s.equals(substring.repeat(s.length() / substring.length()));
        }
    }
}
