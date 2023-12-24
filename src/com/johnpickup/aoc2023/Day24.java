package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 {
    private static final int part = 1;
    private static final boolean test = false;
    private static final BigDecimal MIN_RANGE = test?BigDecimal.valueOf(7) : new BigDecimal("200000000000000");
    private static final BigDecimal MAX_RANGE = test?BigDecimal.valueOf(27) : new BigDecimal("400000000000000");
    private static final String filename = "/Users/john/Development/AdventOfCode/resources/2023/Day24" +
            (test? "-test.txt" : ".txt");
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        int part1 = 0;
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            List<Hailstone> hailstones = lines.stream().map(Hailstone::parse).collect(Collectors.toList());
            System.out.println(hailstones);

            for (int i = 0; i < hailstones.size(); i++) {
                for (int j = 0; j < i; j++) {
                    Hailstone hailstone1 = hailstones.get(j);
                    Hailstone hailstone2 = hailstones.get(i);
                    Intersection intersect = hailstone1.intersectXY(hailstone2);
                    System.out.printf("Intersection of %s and %s is %s%n", hailstone1, hailstone2, intersect);
                    if (intersect != null && inRange(intersect.position, MIN_RANGE, MAX_RANGE)) {
                        part1++;
                    }
                }
            }
            System.out.println(part1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static boolean inRange(Coord position, BigDecimal minRange, BigDecimal maxRange) {
        return position.x.compareTo(minRange)>=0
                && position.x.compareTo(maxRange)<=0
                && position.y.compareTo(minRange)>=0
                && position.y.compareTo(maxRange)<=0;
    }


    static int seq = 0;

    @Data
    static class Hailstone {
        final int id;
        final Coord position;
        final Coord velocity;

        final BigDecimal xySlope;
        final BigDecimal xyOffset;

        public Hailstone(Coord position, Coord velocity) {
            this.id = ++seq;
            this.position = position;
            this.velocity = velocity;
            this.xySlope = velocity.y.divide(velocity.x, MathContext.DECIMAL128);
            this.xyOffset = position.y.subtract(xySlope.multiply(position.x));
        }

        public static Hailstone parse(String s) {
            String[] parts = s.split("@");
            return new Hailstone(Coord.parse(parts[0].trim()), Coord.parse(parts[1].trim()));
        }

        public Intersection intersectXY(Hailstone other) {
            if (this.velocity.equals(other.velocity) && !this.position.equals(other.position)) return null;     // parallel, never intersect unless they start at same point
            if (this.position.equals(other.position)) return new Intersection(this.position, BigDecimal.ZERO);  // start at the same point

            BigDecimal asx = this.position.x;
            BigDecimal asy = this.position.y;
            BigDecimal adx = this.velocity.x;
            BigDecimal ady = this.velocity.y;
            BigDecimal bsx = other.position.x;
            BigDecimal bsy = other.position.y;
            BigDecimal bdx = other.velocity.x;
            BigDecimal bdy = other.velocity.y;

            BigDecimal uDiv = adx.multiply(bdy).subtract(ady.multiply(bdx));
            if (uDiv.equals(BigDecimal.ZERO)) {
                System.out.printf("Divide by zero intersecting %s and %s%n", this, other);
                return null;
            }

            BigDecimal uNum = asy.multiply(bdx).add(bdy.multiply(bsx)).subtract(bsy.multiply(bdx)).subtract(bdy.multiply(asx));
            BigDecimal u = uNum.divide(uDiv, MathContext.DECIMAL128);
            BigDecimal v = asx.add(adx.multiply(u)).subtract(bsx).divide(bdx, MathContext.DECIMAL128);

            if (u.compareTo(BigDecimal.ZERO) < 0 || v.compareTo(BigDecimal.ZERO) < 0) {
                System.out.printf("Intersection of %s and %s is in the past%n", this, other);
                return null;
            }

            BigDecimal x = asx.add(adx.multiply(u));
            BigDecimal y = asy.add(ady.multiply(u));

            return new Intersection(new Coord(x, y, BigDecimal.ZERO), u);
        }

        @Override
        public String toString() {
            return "" + id +
                    ":[p=" + position +
                    ", v=" + velocity+']';
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Intersection {
        final Coord position;
        final BigDecimal time;
    }


    @RequiredArgsConstructor
    @Data
    static class Coord {
        final BigDecimal x;
        final BigDecimal y;
        final BigDecimal z;

        public static Coord parse(String s) {
            String[] parts = s.split(",");
            if (part == 1) {
                return new Coord(new BigDecimal(parts[0].trim()), new BigDecimal(parts[1].trim()), BigDecimal.ZERO);
            } else {
                return new Coord(new BigDecimal(parts[0].trim()), new BigDecimal(parts[1].trim()), new BigDecimal(parts[2].trim()));
            }
        }

        @Override
        public String toString() {
            return "(" +
                    x + ',' + y + ',' + z +
                    ')';
        }
    }
}
