package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24Part2 {
    private static final boolean test = false;
    private static final String filename = "/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day24" +
            (test? "-test.txt" : ".txt");
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            List<Hailstone> hailstones = lines.stream().map(Hailstone::parse).collect(Collectors.toList());
            System.out.println(hailstones);

            List<String> equations = new ArrayList<>();
            List<String> variables = new ArrayList<>();

            variables.add("Px");
            variables.add("Py");
            variables.add("Pz");
            variables.add("Vx");
            variables.add("Vy");
            variables.add("Vz");

            for (int i = 0 ; i < hailstones.size() && i < 6; i++) {
                Hailstone hailstone = hailstones.get(i);
                // solve possible coords
                // rock.position.x + rock.velocity.x*t = hailstone.position.x + hailstone.velocity.x*t
                // rock.position.y + rock.velocity.y*t = hailstone.position.y + hailstone.velocity.y*t
                // rock.position.z + rock.velocity.z*t = hailstone.position.z + hailstone.velocity.z*t
                //

                equations.add(String.format("Px - (%s + t%d * (%s - Vx)) == 0", hailstone.position.x, i, hailstone.velocity.x));
                equations.add(String.format("Py - (%s + t%d * (%s - Vy)) == 0", hailstone.position.y, i, hailstone.velocity.y));
                equations.add(String.format("Pz - (%s + t%d * (%s - Vz)) == 0", hailstone.position.z, i, hailstone.velocity.z));
                variables.add(String.format("t%d", i));
            }

            System.out.printf("Solve[{%s}, {%s}]%n",
                    String.join(",\n", equations),
                    String.join(",", variables));

            // feed into mathematica... (not very satisfactory!)
            // result: {{Px->150345122760494,Py->343916889344399,Pz->183394034557877,Vx->196,Vy->-109,Vz->182,t0->846719128122,t1->630665292925,t2->603832192936,t3->589949162936,t4->244450712419,t5->415908955063}}
            BigDecimal part2 = new BigDecimal("150345122760494").add(new BigDecimal("343916889344399")).add(new BigDecimal("183394034557877"));
            System.out.println(part2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
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
            return new Coord(new BigDecimal(parts[0].trim()), new BigDecimal(parts[1].trim()), new BigDecimal(parts[2].trim()));
        }

        @Override
        public String toString() {
            return "(" +
                    x + ',' + y + ',' + z +
                    ')';
        }
    }
}
