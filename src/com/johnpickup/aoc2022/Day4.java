package com.johnpickup.aoc2022;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {
    public static void main(String[] args) throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day4.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            int fullyContains=0;
            int overlaps=0;
            for (String line : lines) {
                String[] parts = line.split(",");
                Range r1 = new Range(parts[0]);
                Range r2 = new Range(parts[1]);

                if (r1.contains(r2) || r2.contains(r1)) {
                    fullyContains++;
                }

                if (r1.overlaps(r2)) {
                    overlaps++;
                }

            }
            System.out.println(fullyContains);
            System.out.println(overlaps);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public static class Range {
        final int from;
        final int to;

        public boolean contains(Range o) {
            return (from<=o.from) && (to>=o.to);
        }

        public boolean overlaps(Range o) {
            return (from<=o.to) && (to>=o.from);
        }

        public Range(String s) {
            String[] parts = s.split("-");
            this.from = Integer.parseInt(parts[0]);
            this.to = Integer.parseInt(parts[1]);
        }
    }
}
