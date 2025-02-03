package com.johnpickup.aoc2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day1 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Mass> masses = stream
                        .filter(s -> !s.isEmpty())
                        .map(Mass::new)
                        .collect(Collectors.toList());

                long part1 = masses.stream().map(Mass::calcFuel).reduce(0L, Long::sum);
                System.out.println("Part 1: " + part1);
                long part2 = masses.stream().map(Mass::calcTotalFuel).reduce(0L, Long::sum);
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Mass {
        final long value;
        Mass(String line) {
            value = Long.parseLong(line);
        }

        long calcFuel() {
            return calcFuelForMass(value);
        }

        long calcTotalFuel() {
            long fuel = calcFuel();
            long extra = calcFuelForMass(fuel);
            while (extra > 0) {
                fuel += extra;
                extra = calcFuelForMass(extra);
            }
            return fuel;
        }

        long calcFuelForMass(long mass) {
            return mass / 3L - 2L;
        }
    }
}
