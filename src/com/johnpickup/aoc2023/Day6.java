package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day6 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day6.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            // Part 1
            //List<Long> times = Arrays.stream(lines.get(0).split(":")[1].split(" ")).filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());
            //List<Long> distances = Arrays.stream(lines.get(1).split(":")[1].split(" ")).filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());
            // Part2 - expected this not to work without significant changes, as is often the case, but it completes in 165ms, i.e. no optimisation required
            List<Long> times = Arrays.stream(lines.get(0).replace(" ","").split(":")[1].split(" ")).filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());
            List<Long> distances = Arrays.stream(lines.get(1).replace(" ","").split(":")[1].split(" ")).filter(s -> !s.isEmpty()).map(Long::parseLong).collect(Collectors.toList());

            long result = IntStream.range(0, times.size())
                    .mapToObj(i -> new RaceRecord(times.get(i), distances.get(i)))
                    .map(RaceRecord::waysToWinMaths)
                    .reduce(1L, (a,b) -> a*b);
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class RaceRecord {
        final long time;
        final long record;

        public long waysToWin() {
            long result = 0;
            for (long chargeTime=0; chargeTime<=time;chargeTime++) {
                 long d = chargeTime * (time - chargeTime);
                 if (d > record) result++;
            }
            return result;
        }

        // This doesn't quite work - can't find a way to round to longs correctly for all cases
        public long waysToWinMaths() {
            double quad = Math.sqrt(1d * time * time - 4d * record) / -2d;
            double v1 =  quad;
            double v2 =  - quad;
            System.out.println(String.format("%f %f", v1, v2));

            //return (long)(Math.floor(v2) - Math.ceil(v1));
            return Math.round(v2 - v1);
        }
    }
}
