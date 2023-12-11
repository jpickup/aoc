package com.johnpickup.aoc2023;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {
    static int height;
    static int width;
    static final int PART = 2;
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day11.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            height = lines.size();
            width = lines.get(0).length();
            TreeSet<Coord> galaxies = parse(lines);
            print(galaxies, width, height);
            expandGalaxies(galaxies, PART ==2?1000000:1);
            System.out.println(calcTotalDistances(galaxies));

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static void expandGalaxies(TreeSet<Coord> galaxies, int expansion) {
        Set<Integer> xs = galaxies.stream().map(Coord::getX).collect(Collectors.toSet());
        Set<Integer> ys = galaxies.stream().map(Coord::getY).collect(Collectors.toSet());
        // expand
        for (int y = height-1; y>=0; y--) {
            if (!ys.contains(y)) {
                final int Y = y;
                height += expansion;
                galaxies.stream().filter(g -> (g.y > Y)).forEach(c -> c.incYBy(expansion-1));
            }
        }
        for (int x = width-1; x>=0; x--) {
            if (!xs.contains(x)) {
                final int X = x;
                width += expansion;
                galaxies.stream().filter(g -> (g.x > X)).forEach(c -> c.incXBy(expansion-1));
            }
        }

    }

    private static long calcTotalDistances(TreeSet<Coord> galaxies) {
        long result = 0L;
        for (Coord galaxy1 : galaxies) {
            for (Coord galaxy2 : galaxies) {
                if (galaxy1.compareTo(galaxy2) < 0) {
                    int dist = galaxy1.distanceTo(galaxy2);
                    result += dist;
                }
            }
        }
        return result;
    }

    static void print(Set<Coord> coords, int width, int height) {
        System.out.printf("Width = %d, height = %d%n", width, height);
        System.out.printf("Galaxy count = %d%n", coords.size());
        for (int y=0; y < height; y++) {
            for (int x=0; x < width; x++) {
                Coord coord = new Coord(x, y);
                System.out.print(coords.contains(coord) ? "#" : ".");
            }
            System.out.println();
        }
    }

    static TreeSet<Coord> parse(List<String> lines) {
        TreeSet<Coord> result = new TreeSet<>();
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                if (lines.get(y).charAt(x) == '#') {
                    result.add(new Coord(x,y));
                }
            }
        }
        return result;
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    @Getter
    static class Coord implements Comparable<Coord> {
        int x;
        int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void incXBy(int expansion) {
            x += expansion;
        }
        public void incYBy(int expansion) {
            y += expansion;
        }

        @Override
        public int compareTo(Coord o) {
            // order by x then y
            return (o.x - x != 0) ? (o.x - x) : (o.y - y);
        }

        @Override
        public String toString() {
            return "(" + x +
                    "," + y +
                    ')';
        }

        public int distanceTo(Coord o) {
            return Math.abs(x - o.x) + Math.abs(y - o.y);
        }

    }
}
