package com.johnpickup.aoc2024;

import com.johnpickup.aoc2024.util.Coord;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
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

public class Day13Iterative {

    static final long PART_B_OFFSET = 10000000000000L;
    public static void main(String[] args) {
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2024/Day13/Day13";
        List<String> inputFilenames = Arrays.asList(
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
                System.out.println(clawMachines);


                long part1 = clawMachines.stream().map(ClawMachine::prizeCostPart1).reduce(0L, Long::sum);
                System.out.println("Part 1: " + part1);
                BigInteger part2 = clawMachines.stream().map(ClawMachine::prizeCostPart2).reduce(BigInteger.ZERO, BigInteger::add);
                System.out.println("Part 2: " + part2);
                // 2951479051793528258240 is too high

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
// there are no instances where button B is 3 times better than button A
//            double xRatio = (double)(buttonB.delta.getX()) / (double)(buttonA.delta.getX());
//            double yRatio = (double)(buttonB.delta.getY()) / (double)(buttonA.delta.getY());
//            if (xRatio > 3 && yRatio > 3) {
//                System.out.printf("!!!!!!  %f %f %n", xRatio, yRatio);
//            }
        }

        public Possibility prizePossibilityFromOffset(Coord offset, int maxIter) {
            Coord offsetPrizeLocation = new Coord(
                    prize.location.getX() + offset.getX(),
                    prize.location.getY() + offset.getY());
            for (int b = 0; b < maxIter; b++) {
                for (int a = 0; a < maxIter; a++) {
                    if (calcLocation(a,b).equals(offsetPrizeLocation))
                        return new Possibility(a, b, offset);
                }
            }
            return null;
        }

        public long prizeCostPart1() {
            return Optional.ofNullable(prizePossibilityFromOffset(new Coord(0,0), 100)).map(Possibility::priceCost).orElse(0L);
        }

        private Coord calcLocation(int a, int b) {
            int x = buttonA.delta.getX() * a + buttonB.delta.getX() * b;
            int y = buttonA.delta.getY() * a + buttonB.delta.getY() * b;
            return new Coord(x, y);
        }

        public BigInteger prizeCostPart2() {
            // the offset 10000000000000 will have certain remainders of combinations of the X & Y pairs
            // try them all
            // the GCD & LCM of the two deltas gives us something?
//            long lcmX = lcm(buttonA.delta.getX(), buttonB.delta.getX());
//            long lcmY = lcm(buttonA.delta.getY(), buttonB.delta.getY());
//            long gcdX = gcd(buttonA.delta.getX(), buttonB.delta.getX());
//            long gcdY = gcd(buttonA.delta.getY(), buttonB.delta.getY());
//            System.out.printf("LCM: %d %d %n", lcmX, lcmY);
//            System.out.printf("GCD: %d %d %n", gcdX, gcdY);

            // we actually only need to look at the possibilities based on offsets of the remainder, which is the size-1
            // of the button jumps
            int maxX = Math.max(buttonA.delta.getX(), buttonB.delta.getX());
            int maxY = Math.max(buttonA.delta.getY(), buttonB.delta.getY());

            List<Possibility> possibilities = new ArrayList<>();

            for (int xOffset = 0; xOffset < maxX; xOffset++) {
                for (int yOffset = 0; yOffset < maxY; yOffset++) {
                    Coord offset = new Coord(xOffset, yOffset);
                    Possibility possibility = prizePossibilityFromOffset(offset, 100);
                    if (possibility != null) {
                        possibilities.add(possibility);
                    }
                }
            }
            System.out.printf("Possibilities %d %n", possibilities.size());

            long bestPrice = Long.MAX_VALUE;
            // for each possibility we now have an offset and that can be achieved with a certain (large) number of button presses
            // figure those out
            // because A cost = 3 * B cost we want the most number of Bs - depends on how many Bs in an A - still easy
            for (Possibility possibility : possibilities) {
                Optional<Presses> pressesForOffset = Optional.ofNullable(calcMinPressesForOffset(possibility.offset));

                Optional<Long> price = pressesForOffset.map(possibility::priceCostWithInitialPresses);
                if (price.isPresent() && price.get() < bestPrice) {
                        bestPrice = price.get();
                }
            }
            return BigInteger.valueOf(bestPrice);
        }

        static final double EPSILON = 1e-8;
        private Presses calcMinPressesForOffset(Coord offset) {
            long xTarget = offset.getX() + PART_B_OFFSET;
            long yTarget = offset.getY() + PART_B_OFFSET;
            long ax = buttonA.delta.getX();
            long ay = buttonA.delta.getY();
            long bx = buttonA.delta.getX();
            long by = buttonA.delta.getY();
            // find integers aCount and bCount where the following are true
            //      xTarget = aCount * ax + bCount * bx
            //      yTarget = aCount * ay + bCount * by
            // null if there are none

            double aCount = (xTarget - yTarget * 1d * bx / by) / (ax - ay * 1d * bx / by);
            double bCount = (yTarget - aCount * ay) / by;

            if ((aCount - Math.floor(aCount) < EPSILON) && (bCount - Math.floor(bCount) < EPSILON))
                return new Presses((long)aCount, (long)bCount);
            else
                return null;
        }

        @RequiredArgsConstructor
        @ToString
        static class Possibility {
            final long aPresses;
            final long bPresses;
            final Coord offset;

            long priceCost() {
                return priceCostWithInitialPresses(new Presses(0,0));
            }

            long priceCostWithInitialPresses(Presses initialPresses) {
                return (initialPresses.aPresses + aPresses) * A_COST + (initialPresses.bPresses + bPresses) * B_COST;
            }
        }

        @RequiredArgsConstructor
        @ToString
        static class Presses {
            final long aPresses;
            final long bPresses;
        }

        static long gcd(long a, long b) {
            return BigInteger.valueOf(a).gcd(BigInteger.valueOf(b)).longValue();
        }
        static long lcm(long a, long b) {
            return lcm(BigInteger.valueOf(a), BigInteger.valueOf(b)).longValue();
        }
        static BigInteger lcm(BigInteger x, BigInteger y) {
            return x.multiply(y).divide(x.gcd(y));
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
