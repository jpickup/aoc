package com.johnpickup.aoc2021;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22ReactorPart2 {
    List<String> lines;

    public static void main(String[] args) {
        Day22ReactorPart2 day22 =
                new Day22ReactorPart2("/Volumes/Users/john/Development/AdventOfCode/resources/Day22Input.txt");

        //187500

        day22.solve();
    }

    private void solve() {
        List<Volume> instructions = lines.stream().map(this::parse).filter(Objects::nonNull).collect(Collectors.toList());
        instructions.forEach(System.out::println);

        Set<Volume> explodedVolumes = new HashSet<>();

        for (Volume instruction : instructions) {
            explodedVolumes = addToVolumes(instruction, explodedVolumes);
            System.out.println(explodedVolumes.size());
        }

        System.out.println("Exploded -----");
        explodedVolumes.forEach(System.out::println);

        BigInteger result = explodedVolumes.stream()
                .filter(v -> v.operation)
                .map(Volume::volume)
                .reduce(BigInteger.ZERO, BigInteger::add);

        System.out.println(result);
    }

    private Set<Volume> addToVolumes(Volume newVolume, Set<Volume> volumes) {
        if (volumes.isEmpty()) return Collections.singleton(newVolume);
        return newVolume.intersectExplode(volumes);
    }

    private Volume parse(String s) {
        if (s.isEmpty()) return null;
        boolean op = s.startsWith("on");
        String rest = s.split(" ")[1];
        String[] parts = rest.split(",");

        return new Volume(op, parseMin(parts[0]), parseMax(parts[0])+1,
                parseMin(parts[1]), parseMax(parts[1])+1,
                parseMin(parts[2]), parseMax(parts[2])+1);
    }

    private int parseMin(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[0]);
    }

    private int parseMax(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[1]);
    }

    public Day22ReactorPart2(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Volume {
        boolean operation;
        int minX, maxX, minY, maxY, minZ, maxZ;

        public Volume(boolean operation, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
            this.operation = operation;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.minZ = minZ;
            this.maxZ = maxZ;
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
                    maxY == that.maxY &&
                    minZ == that.minZ &&
                    maxZ == that.maxZ;
        }

        @Override
        public int hashCode() {
            return Objects.hash(operation, minX, maxX, minY, maxY, minZ, maxZ);
        }

        @Override
        public String toString() {
            return (operation ? "on " : "off") + " " +
                    "X=" + minX +
                    ".." + maxX +
                    ",Y=" + minY +
                    ".." + maxY +
                    ",Z=" + minZ +
                    ".." + maxZ;
        }

        public boolean empty() {
            return (maxX <= minX) || (maxY <= minY) || (maxZ <= minZ);
        }

        public BigInteger volume() {
            BigInteger xdiff = BigInteger.valueOf(maxX - minX);
            BigInteger ydiff = BigInteger.valueOf(maxY - minY);
            BigInteger zdiff = BigInteger.valueOf(maxZ - minZ);

            return xdiff.multiply(ydiff).multiply(zdiff);
        }

        public boolean intersects(Volume other) {
            int biggestMinX = Math.max(minX, other.minX);
            int smallestMaxX = Math.min(maxX, other.maxX);
            int biggestMinY = Math.max(minY, other.minY);
            int smallestMaxY = Math.min(maxY, other.maxY);
            int biggestMinZ = Math.max(minZ, other.minZ);
            int smallestMaxZ = Math.min(maxZ, other.maxZ);

            return (biggestMinX < smallestMaxX) && (biggestMinY < smallestMaxY) && (biggestMinZ < smallestMaxZ);
        }

        public Set<Volume> intersectExplode(Set<Volume> others) {

            Set<Volume> intersecting = others.stream().filter(o -> o.intersects(this)).collect(Collectors.toSet());
            Set<Volume> nonIntersecting = others.stream().filter(o -> !o.intersects(this)).collect(Collectors.toSet());

            Set<Volume> result = new HashSet<>(nonIntersecting);

            Set<Integer> xsS = intersecting.stream().flatMap(v -> Stream.of(v.minX, v.maxX)).collect(Collectors.toSet());
            xsS.addAll(Arrays.asList(this.minX, this.maxX));

            Set<Integer> ysS = intersecting.stream().flatMap(v -> Stream.of(v.minY, v.maxY)).collect(Collectors.toSet());
            ysS.addAll(Arrays.asList(this.minY, this.maxY));

            Set<Integer> zsS = intersecting.stream().flatMap(v -> Stream.of(v.minZ, v.maxZ)).collect(Collectors.toSet());
            zsS.addAll(Arrays.asList(this.minZ, this.maxZ));

            List<Integer> xs = new ArrayList<>(xsS);
            List<Integer> ys = new ArrayList<>(ysS);
            List<Integer> zs = new ArrayList<>(zsS);

            xs.sort(Integer::compareTo);
            ys.sort(Integer::compareTo);
            zs.sort(Integer::compareTo);

            for (int i = 0; i < xs.size()-1; i++) {
                for (int j = 0; j < ys.size()-1; j++) {
                    for (int k = 0; k < zs.size()-1; k++) {
                        // we now have pairs of Xs, Ys and Zs - use them to create volumes
                        Volume temp = new Volume(true,
                                xs.get(i), xs.get(i + 1),
                                ys.get(j), ys.get(j + 1),
                                zs.get(k), zs.get(k + 1));

                        boolean newOp = !temp.intersects(this) || this.operation;
                        Volume newVolume = new Volume(newOp,
                                xs.get(i), xs.get(i + 1),
                                ys.get(j), ys.get(j + 1),
                                zs.get(k), zs.get(k + 1));

                        if (newVolume.operation) {
                            // only add this volume if it is within the two generators, i.e. exclude those that are not
                            boolean intersectsOthers = intersecting.stream().anyMatch(o -> o.intersects(newVolume));
                            if (!newVolume.empty() && (newVolume.intersects(this) || intersectsOthers)) {
                                result.add(newVolume);
                            }
                        }

                    }
                }
            }
            return result;
        }
    }

}
