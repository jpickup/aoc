package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.Coord;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13 {
    static final long PART_2_OFFSET = 10000000000000L;
    public static void main(String[] args) {
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/Day13/Day13";
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-problems.txt"
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .collect(Collectors.toList());

                List<String> lineGroup = new ArrayList<>();
                List<ClawMachine> clawMachines = new ArrayList<>();
                for (String line : lines) {
                    if (line.isEmpty()) {
                        ClawMachine clawMachine = new ClawMachine(lineGroup);
                        clawMachines.add(clawMachine);
                        lineGroup.clear();
                    }
                    else {
                        lineGroup.add(line);
                    }
                }

                long part1 = clawMachines.stream().map(ClawMachine::prizeCostPart1).reduce(0L, Long::sum);
                System.out.println("Part 1: " + part1);
                BigInteger part2 = clawMachines.stream().map(ClawMachine::prizeCostPart2).reduce(BigInteger.ZERO, BigInteger::add);
                System.out.println("Part 2: " + part2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class ClawMachine {
        final Button buttonA;
        final Button buttonB;
        final Prize prize;

        static final long A_COST = 3;
        static final long B_COST = 1;

        ClawMachine(List<String> lines) {
            if (lines.size() != 3) throw new RuntimeException("Invalid lines from ClawMachine");
            buttonA = new Button(lines.get(0));
            buttonB = new Button(lines.get(1));
            prize = new Prize(lines.get(2));
        }

        public long prizeCostPart1() {
            Optional<Presses> presses = Optional.ofNullable(calcPressesForLocation(prize.location, 0L));
            return presses.map(Presses::cost).orElse(0L);
        }

        public BigInteger prizeCostPart2() {
            Optional<Presses> presses = Optional.ofNullable(calcPressesForLocation(prize.location, PART_2_OFFSET));
            return BigInteger.valueOf(presses.map(Presses::cost).orElse(0L));
        }

        private Presses calcPressesForLocation(Coord location, long offset) {
            // find integers aCount and bCount where the following are true
            //      xTarget = aCount * ax + bCount * bx
            //      yTarget = aCount * ay + bCount * by
            // null if there are none
            BigDecimal xTarget = BigDecimal.valueOf(location.getX() + offset);
            BigDecimal yTarget = BigDecimal.valueOf(location.getY() + offset);
            BigDecimal ax = BigDecimal.valueOf(buttonA.delta.getX());
            BigDecimal ay = BigDecimal.valueOf(buttonA.delta.getY());
            BigDecimal bx = BigDecimal.valueOf(buttonB.delta.getX());
            BigDecimal by = BigDecimal.valueOf(buttonB.delta.getY());

            // a presses = (xTarget - yTarget * bx / by) / (ax - ay * bx / by) is an integer
            // simplifies to (xTarget * by - yTarget * bx) / (ax * by - ay * bx)
            BigDecimal aCountNumerator = xTarget.multiply(by).subtract(yTarget.multiply(bx));
            BigDecimal aCountDenominator = ax.multiply(by).subtract(ay.multiply(bx));
            BigDecimal[] aCountDivideAndRemainder = aCountNumerator.divideAndRemainder(aCountDenominator);
            BigDecimal aCount = aCountDivideAndRemainder[0];
            BigDecimal remainder = aCountDivideAndRemainder[1];

            boolean isAInteger = remainder.equals(BigDecimal.ZERO);
            if (!isAInteger) return null;

            // b presses = (yTarget - aCount * ay) / by
            BigDecimal bCountNumerator = yTarget.subtract(aCount.multiply(ay));
            BigDecimal[] bCountDivideAndRemainder = bCountNumerator.divideAndRemainder(by);

            BigDecimal bCountBD = bCountDivideAndRemainder[0];
            BigDecimal bRemainder = bCountDivideAndRemainder[1];

            boolean isBInteger = bRemainder.equals(BigDecimal.ZERO);
            if (!isBInteger) return null;

            long bCount = bCountBD.longValue();
            return new Presses(aCount.longValue(), bCount);
        }

        @RequiredArgsConstructor
        @ToString
        static class Presses {
            final long aPresses;
            final long bPresses;

            public long cost() {
                return aPresses * A_COST + bPresses * B_COST;
            }
        }
    }

    @ToString
    static class Button {
        final char label;
        final Coord delta;
        static final Pattern pattern = Pattern.compile("Button (A|B): X\\+([0-9]+), Y\\+([0-9]+)");
        Button(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException("Invalid line for button: " + line);
            int x = Integer.parseInt(matcher.group(2));
            int y = Integer.parseInt(matcher.group(3));
            label = matcher.group(1).charAt(0);
            delta = new Coord(x, y);
        }
    }

    @ToString
    static class Prize {
        final Coord location;
        static final Pattern pattern = Pattern.compile("Prize: X=([0-9]+), Y=([0-9]+)");
        Prize(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException("Invalid line for prize: " + line);
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            location = new Coord(x, y);
        }
    }
}

/* didn't need in the end
        static long gcd(long a, long b) {
            return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValue();
        }
        static long lcm(long a, long b) {
            return lcm(BigInteger.valueOf(a), BigInteger.valueOf(b)).longValue();
        }
        static BigInteger lcm(BigInteger x, BigInteger y) {
            return x.multiply(y).divide(x.gcd(y));
        }
 */
