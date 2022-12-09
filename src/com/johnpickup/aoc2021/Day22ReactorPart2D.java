package com.johnpickup.aoc2021;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22ReactorPart2D {
    List<String> lines;

    public static void main(String[] args) {
        Day22ReactorPart2D day22 =
                new Day22ReactorPart2D("/Users/john/Development/AdventOfCode/resources/Day22ErrAdd.txt");

        //ErrAdd target: 2928

        day22.solve();
    }

    private void solve() {
        List<Volume> instructions = lines.stream().map(this::parse).filter(Objects::nonNull).collect(Collectors.toList());
        instructions.forEach(System.out::println);

        Set<Volume> explodedVolumes = new HashSet<>();

        for (Volume instruction : instructions) {
            explodedVolumes = addToVolumes(instruction, explodedVolumes);
        }

        System.out.println("Exploded -----");
        explodedVolumes.forEach(System.out::println);

        BigDecimal result = explodedVolumes.stream()
                .filter(v -> v.operation)
                .map(Volume::volume)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println(result);
    }

    private Set<Volume> addToVolumes(Volume newVolume, Set<Volume> volumes) {
        if (volumes.isEmpty()) return Collections.singleton(newVolume);

        Set<Volume> result = new HashSet<>();

        for (Volume existingVolume : volumes) {
            result.addAll(newVolume.intersectExplode(existingVolume));
        }

        return result;
    }

    private Volume parse(String s) {
        if (s.isEmpty()) return null;
        boolean op = s.startsWith("on");
        String rest = s.split(" ")[1];
        String[] parts = rest.split(",");

        return new Volume(op, parseMin(parts[0]), parseMax(parts[0]), parseMin(parts[1]), parseMax(parts[1]));
    }

    private int parseMin(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[0]);
    }

    private int parseMax(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[1]);
    }

    public Day22ReactorPart2D(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Volume {
        boolean operation;
        int minX, maxX, minY, maxY;

        public Volume(boolean operation, int minX, int maxX, int minY, int maxY) {
            this.operation = operation;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Volume that = (Volume) o;
            return operation == that.operation &&
                    minX == that.minX &&
                    maxX == that.maxX &&
                    minY == that.minY &&
                    maxY == that.maxY;
        }

        @Override
        public int hashCode() {
            return Objects.hash(operation, minX, maxX, minY, maxY);
        }

        @Override
        public String toString() {
            return (operation ? "on " : "off") + " " +
                    "X=" + minX +
                    "..." + maxX +
                    ",Y=" + minY +
                    "..." + maxY + "  = " + volume();
        }

        public boolean empty() {
            return (maxX == minX) || (maxY == minY);
        }

        public BigDecimal volume() {
            BigDecimal xdiff = BigDecimal.valueOf(maxX+1 - minX );
            BigDecimal ydiff = BigDecimal.valueOf(maxY+1 - minY );

            return xdiff.multiply(ydiff);
        }

        public boolean intersects(Volume other) {
            int biggestMinX = Math.max(minX, other.minX);
            int smallestMaxX = Math.min(maxX, other.maxX);
            int biggestMinY = Math.max(minY, other.minY);
            int smallestMaxY = Math.min(maxY, other.maxY);

            return (biggestMinX < smallestMaxX) && (biggestMinY < smallestMaxY) ;
        }

        public Set<Volume> intersectExplode(Volume other) {
            if (!this.intersects(other))
                return new HashSet<>(Arrays.asList(this, other)).stream()
                        .filter(v -> v.operation).collect(Collectors.toSet());

            Set<Volume> result = new HashSet<>();

            List<Integer> xs = Arrays.asList(this.minX, this.maxX-1, other.minX, other.maxX-1);
            List<Integer> ys = Arrays.asList(this.minY, this.maxY-1, other.minY, other.maxY-1);
            xs.sort(Integer::compareTo);
            ys.sort(Integer::compareTo);

            for (int i = 0; i < xs.size()-1; i++) {
                for (int j = 0; j < ys.size()-1; j++) {
                        // we now have pairs of Xs, Ys and Zs - use them to create volumes
                        Volume temp = new Volume(true,
                                xs.get(i), xs.get(i + 1)+1,
                                ys.get(j), ys.get(j + 1)+1);

                        boolean newOp = temp.intersects(this) ? this.operation : other.operation;
                        Volume newVolume = new Volume(newOp,
                                xs.get(i), xs.get(i + 1)+1,
                                ys.get(j), ys.get(j + 1)+1);

                        // only add this volume if it is within the two generators, i.e. exclude those that are not
                        if (!newVolume.empty() && (newVolume.intersects(this) || newVolume.intersects(other))) {
                            result.add(newVolume);
                        }
                }
            }
            return result;
        }
    }

}
