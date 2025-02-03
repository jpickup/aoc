package com.johnpickup.aoc2020;

import com.johnpickup.util.ChineseRemainderTheorem;
import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day13 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
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
                Timetable timetable = new Timetable(lines);

                System.out.println(timetable);
                long part1 = timetable.part1();
                System.out.println("Part 1: " + part1);
                long part2 = timetable.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }

    }
    @ToString
    static class Timetable {
        final List<Bus> buses;
        final long departureTime;
        Timetable(List<String> lines) {
            departureTime = Long.parseLong(lines.get(0));
            buses = Arrays.stream(lines.get(1).split(","))
                    .map(Bus::new)
                    .collect(Collectors.toList());
        }

        long part1() {
            Optional<Departure> earliest = buses.stream()
                    .filter(Bus::isValid)
                    .map(b -> b.departureForTarget(departureTime))
                    .min(Departure::compareTo);
            return earliest.map(Departure::part1).orElseThrow(() -> new RuntimeException("No departure found"));
        }

        long part2() {
            List<Long> qs = buses.stream().filter(Bus::isValid).map(b -> b.number).collect(Collectors.toList());
            List<Long> as = new ArrayList<>();
            int qIdx = 0;
            for (int i = 0; i < buses.size(); i++) {
                Bus bus = buses.get(i);
                if (bus.isValid()) {
                    as.add(i == 0 ? 0 : qs.get(qIdx) - i);
                    qIdx++;
                }
            }

            return ChineseRemainderTheorem.calculateLong(as, qs);
        }
    }

    @Data
    static class Bus {
        final Long number;

        Bus(String s) {
            number = s.equals("x") ? null : Long.parseLong(s);
        }

        public Departure departureForTarget(long departureTime) {
            if (number == null) return null;
            long missedBy = departureTime % number;
            return new Departure(this, departureTime, number - missedBy);
        }

        public boolean isValid() {
            return number != null;
        }

        public boolean matches(long time) {
            return number == null || (time % number == 0);
        }
    }

    @Data
    static class Departure implements Comparable<Departure> {
        final Bus bus;
        final long target;
        final long wait;

        long part1() {
            return bus.number * wait;
        }
        @Override
        public int compareTo(Departure o) {
            return Long.compare(wait, o.wait);
        }
    }

}
