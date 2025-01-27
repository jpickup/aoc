package com.johnpickup.aoc2018;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day4 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Users/john/Development/AdventOfCode/resources/2018/" + day + "/" + day;
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
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .sorted(String::compareTo)
                        .collect(Collectors.toList());

                Map<Integer, Guard> guards = new HashMap<>();
                int currentGuardId;
                Guard currentGuard = null;
                DateTime sleepTime = null;
                for (String line : lines) {
                    DateTime eventDateTime = new DateTime(line.substring(line.indexOf('[')+1, line.indexOf(']')));
                    if (line.contains("begins shift"))  {
                        currentGuardId = Integer.parseInt(line.substring(line.indexOf('#')+1).replaceAll(" begins shift",""));
                        guards.putIfAbsent(currentGuardId, new Guard(currentGuardId));
                        currentGuard = guards.get(currentGuardId);
                    } else if (line.contains("falls asleep")) {
                        sleepTime = eventDateTime;
                    } else if (line.contains("wakes up")) {
                        if (currentGuard == null) throw new RuntimeException("no current guard");
                        if (sleepTime == null) throw new RuntimeException("invalid sleep time");
                        currentGuard.addSleepPeriod(new Period(sleepTime, eventDateTime));
                    }
                }

                Guard mostAsleepGuard = guards.values().stream()
                        .sorted(Comparator.comparingLong(Guard::minutesAsleep))
                        .reduce((a, b) -> b)
                        .orElseThrow(() -> new RuntimeException("No guard was asleep"));
                int minute = mostAsleepGuard.mostAsleepMinute();
                System.out.println("Part 1: " + mostAsleepGuard.id * minute);

                Guard mostAsleepGuardAtAnySingleTime = guards.values().stream()
                        .sorted(Comparator.comparingLong(Guard::mostAsleepCount))
                        .reduce((a, b) -> b)
                        .orElseThrow(() -> new RuntimeException("No guard was asleep"));
                int minute2 = mostAsleepGuardAtAnySingleTime.mostAsleepMinute();
                System.out.println("Part 2: " + mostAsleepGuardAtAnySingleTime.id * minute2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @Data
    static class Guard {
        final int id;
        final List<Period> sleepPeriods = new ArrayList<>();

        public void addSleepPeriod(Period period) {
            sleepPeriods.add(period);
        }

        public long minutesAsleep() {
            return sleepPeriods.stream().map(Period::duration).reduce(0L, Long::sum);
        }

       public int mostAsleepMinute() {
            int result = 0;
            int max = 0;
            for (int i=0; i < 60; i++) {
                AtomicInteger a = new AtomicInteger(i);
                long times = sleepPeriods.stream().filter(p -> p.covers(a.get())).count();
                if (times > max) {
                    result = i;
                    max = (int)times;
                }
            }
            return result;
        }

        public int mostAsleepCount() {
            int max = 0;
            for (int i=0; i < 60; i++) {
                AtomicInteger a = new AtomicInteger(i);
                long times = sleepPeriods.stream().filter(p -> p.covers(a.get())).count();
                if (times > max) {
                    max = (int)times;
                }
            }
            return max;
        }
    }

    @Data
    static class Period {
        final DateTime start;
        final DateTime end;

        public long duration() {
            return end.time.minute - start.time.minute;
        }

        public boolean covers(int minute) {
            return start.time.minute <= minute
                    && end.time.minute > minute;
        }
    }

    @Data
    static class DateTime {
        final Date date;
        final Time time;
        DateTime(String s) {
            String[] parts = s.split(" ");
            date = new Date(parts[0]);
            time = new Time(parts[1]);
        }
    }

    @Data
    static class Time {
        final int hour;
        final int minute;
        Time(String s) {
            String[] parts = s.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        }
    }

    @Data
    static class Date {
        final int year;
        final int month;
        final int day;
        Date(String s) {
            String[] parts = s.split("-");
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            day = Integer.parseInt(parts[2]);
        }
    }
}
