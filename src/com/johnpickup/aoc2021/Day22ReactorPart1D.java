package com.johnpickup.aoc2021;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22ReactorPart1D {
    List<String> lines;

    public static void main(String[] args) {
        Day22ReactorPart1D day22 =
                new Day22ReactorPart1D("/Volumes/Users/john/Development/AdventOfCode/resources/Day22Test.txt");

        //ErrAdd target: 2928

        day22.solve();
    }

    private void solve() {
        List<Volume> instructions = lines.stream().map(this::parse).filter(Objects::nonNull).collect(Collectors.toList());
        instructions.forEach(System.out::println);

        Set<Volume> explodedVolumes = new HashSet<>();

        for (Volume instruction : instructions) {
            explodedVolumes = addToVolumes(instruction, explodedVolumes);
            System.out.println("Exploded Step -----");
            explodedVolumes.forEach(System.out::println);
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

        result.addAll(newVolume.intersectExplode(volumes));

        return result;
    }

    private Volume parse(String s) {
        if (s.isEmpty()) return null;
        boolean op = s.startsWith("on");
        String rest = s.split(" ")[1];
        String[] parts = rest.split(",");

        return new Volume(op, parseMin(parts[0]), parseMax(parts[0])+1);
    }

    private int parseMin(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[0]);
    }

    private int parseMax(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[1]);
    }

    public Day22ReactorPart1D(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Volume {
        boolean operation;
        int minX, maxX;

        public Volume(boolean operation, int minX, int maxX) {
            this.operation = operation;
            this.minX = minX;
            this.maxX = maxX;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Volume that = (Volume) o;
            return operation == that.operation &&
                    minX == that.minX &&
                    maxX == that.maxX;
        }

        @Override
        public int hashCode() {
            return Objects.hash(operation, minX, maxX);
        }

        @Override
        public String toString() {
            return (operation ? "on " : "off") + " " +
                    "X=" + minX +
                    "..." + maxX + "  = " + volume();
        }

        public boolean empty() {
            return (maxX <= minX);
        }

        public BigDecimal volume() {
            BigDecimal xdiff = BigDecimal.valueOf(maxX - minX);

            return xdiff;
        }

        public boolean intersects(Volume other) {
            int biggestMinX = Math.max(minX, other.minX);
            int smallestMaxX = Math.min(maxX, other.maxX);

            return (biggestMinX < smallestMaxX);
        }

        public Set<Volume> intersectExplode(Set<Volume> others) {
            Set<Volume> result = new HashSet<>();

            List<Integer> xs = others.stream().flatMap(v -> Stream.of(v.minX, v.maxX)).collect(Collectors.toList());
            xs.addAll(Arrays.asList(this.minX, this.maxX));

            xs.sort(Integer::compareTo);

            System.out.println(xs);
            for (int i = 0; i < xs.size()-1; i++) {
                        // we now have pairs of Xs, Ys and Zs - use them to create volumes
                        Volume temp = new Volume(true,
                                xs.get(i), xs.get(i + 1));

                        boolean newOp = !temp.intersects(this) || this.operation;
                        Volume newVolume = new Volume(newOp,
                                xs.get(i), xs.get(i + 1));

                        // only add this volume if it is within the two generators, i.e. exclude those that are not
                boolean intersectsOthers = others.stream().anyMatch(o -> o.intersects(newVolume));
                if (newVolume.operation && !newVolume.empty() && (newVolume.intersects(this) || intersectsOthers)) {
                            result.add(newVolume);
                        }
            }
            return result;
        }
    }

}
