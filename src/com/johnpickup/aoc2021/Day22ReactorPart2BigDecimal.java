package com.johnpickup.aoc2021;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22ReactorPart2BigDecimal {
    List<String> lines;

    public static void main(String[] args) {
        Day22ReactorPart2BigDecimal day22 =
                new Day22ReactorPart2BigDecimal("/Users/john/Development/AdventOfCode/resources/Day22MyTest.txt");

        //187500

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

        return new Volume(op, parseMin(parts[0]), parseMax(parts[0]), parseMin(parts[1]), parseMax(parts[1]), parseMin(parts[2]), parseMax(parts[2]));
    }

    private int parseMin(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[0]);
    }

    private int parseMax(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[1]);
    }

    public Day22ReactorPart2BigDecimal(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Volume {
        boolean operation;
        BigDecimal minX, maxX, minY, maxY, minZ, maxZ;
        static final BigDecimal HALF = new BigDecimal("0.5");

        public Volume(boolean operation, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
            this.operation = operation;
            this.minX = BigDecimal.valueOf(minX).subtract(HALF);
            this.maxX = BigDecimal.valueOf(maxX).add(HALF);
            this.minY = BigDecimal.valueOf(minY).subtract(HALF);
            this.maxY = BigDecimal.valueOf(maxY).add(HALF);
            this.minZ = BigDecimal.valueOf(minZ).subtract(HALF);
            this.maxZ = BigDecimal.valueOf(maxZ).add(HALF);
        }

        public Volume(boolean operation, BigDecimal minX, BigDecimal maxX, BigDecimal minY, BigDecimal maxY, BigDecimal minZ, BigDecimal maxZ) {
            this.operation = operation;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }


        @Override
        public String toString() {
            return (operation ? "on " : "off") + " " +
                    "X=" + minX +
                    ".." + maxX +
                    ",Y=" + minY +
                    ".." + maxY +
                    ",Z=" + minZ +
                    ".." + maxZ +
                    "  (= " + volume() + ")";
        }

        public boolean empty() {
            BigDecimal xdiff = maxX.subtract(minX);
            BigDecimal ydiff = maxY.subtract(minY);
            BigDecimal zdiff = maxZ.subtract(minZ);

            return xdiff.compareTo(BigDecimal.ZERO)==0 || ydiff.compareTo(BigDecimal.ZERO)==0 || zdiff.compareTo(BigDecimal.ZERO)==0;
        }

        public BigDecimal volume() {
            BigDecimal xdiff = maxX.subtract(minX);
            BigDecimal ydiff = maxY.subtract(minY);
            BigDecimal zdiff = maxZ.subtract(minZ);

            return xdiff.multiply(ydiff).multiply(zdiff);
        }

        public boolean intersects(Volume other) {
            BigDecimal biggestMinX =  minX.compareTo(other.minX)>0?minX:other.minX;
            BigDecimal smallestMaxX = maxX.compareTo(other.maxX)<0?maxX:other.maxX;
            BigDecimal biggestMinY = minY.compareTo(other.minY)>0?minY:other.minY;
            BigDecimal smallestMaxY = maxY.compareTo(other.maxY)<0?maxY:other.maxY;
            BigDecimal biggestMinZ = minZ.compareTo(other.minZ)>0?minZ:other.minZ;
            BigDecimal smallestMaxZ = maxZ.compareTo(other.maxZ)<0?maxZ:other.maxZ;

            return (biggestMinX.compareTo(smallestMaxX)<0) && (biggestMinY.compareTo(smallestMaxY)<0) && (biggestMinZ.compareTo(smallestMaxZ)<0);
        }

        public BigDecimal intersectionVolume(Volume other) {
            if (!intersects(other)) return BigDecimal.ZERO;

            BigDecimal biggestMinX =  minX.compareTo(other.minX)>0?minX:other.minX;
            BigDecimal smallestMaxX = maxX.compareTo(other.maxX)<0?maxX:other.maxX;
            BigDecimal biggestMinY = minY.compareTo(other.minY)>0?minY:other.minY;
            BigDecimal smallestMaxY = maxY.compareTo(other.maxY)<0?maxY:other.maxY;
            BigDecimal biggestMinZ = minZ.compareTo(other.minZ)>0?minZ:other.minZ;
            BigDecimal smallestMaxZ = maxZ.compareTo(other.maxZ)<0?maxZ:other.maxZ;

            BigDecimal xdiff = biggestMinX.subtract(smallestMaxX);
            BigDecimal ydiff = biggestMinY.subtract(smallestMaxY);
            BigDecimal zdiff = biggestMinZ.subtract(smallestMaxZ);

            return xdiff.multiply(ydiff).multiply(zdiff);
        }

        public Volume intersection(Volume other) {
            if (!intersects(other)) return null;

            BigDecimal biggestMinX =  minX.compareTo(other.minX)>0?minX:other.minX;
            BigDecimal smallestMaxX = maxX.compareTo(other.maxX)<0?maxX:other.maxX;
            BigDecimal biggestMinY = minY.compareTo(other.minY)>0?minY:other.minY;
            BigDecimal smallestMaxY = maxY.compareTo(other.maxY)<0?maxY:other.maxY;
            BigDecimal biggestMinZ = minZ.compareTo(other.minZ)>0?minZ:other.minZ;
            BigDecimal smallestMaxZ = maxZ.compareTo(other.maxZ)<0?maxZ:other.maxZ;

            // if this is the volume we are intersecting with other then the operation of this is also the operation
            // of the result (adding this volume = adding the intersection, removing this volume = removing the intersection
            boolean newOp = operation;
            return new Volume(newOp, biggestMinX, smallestMaxX, biggestMinY, smallestMaxY, biggestMinZ, smallestMaxZ);
        }

        public Set<Volume> intersectExplode(Volume other) {
            if (!this.intersects(other)) return new HashSet<>(Arrays.asList(this, other));

            Set<Volume> result = new HashSet<>();

            List<BigDecimal> xs = Arrays.asList(this.minX, this.maxX, other.minX, other.maxX);
            List<BigDecimal> ys = Arrays.asList(this.minY, this.maxY, other.minY, other.maxY);
            List<BigDecimal> zs = Arrays.asList(this.minZ, this.maxZ, other.minZ, other.maxZ);
            xs.sort(BigDecimal::compareTo);
            ys.sort(BigDecimal::compareTo);
            zs.sort(BigDecimal::compareTo);

            for (int i = 0; i < xs.size()-1; i++) {
                for (int j = 0; j < ys.size()-1; j++) {
                    for (int k = 0; k < zs.size()-1; k++) {
                        // we now have pairs of Xs, Ys and Zs - use them to create volumes
                        Volume temp = new Volume(true,
                                xs.get(i), xs.get(i + 1),
                                ys.get(j), ys.get(j + 1),
                                zs.get(k), zs.get(k + 1));

                        boolean newOp = temp.intersects(this) ? this.operation : other.operation;
                        Volume newVolume = new Volume(newOp,
                                xs.get(i), xs.get(i + 1),
                                ys.get(j), ys.get(j + 1),
                                zs.get(k), zs.get(k + 1));

                        // only add this volume if it is within the two generators, i.e. exclude those that are not
                        if (!newVolume.empty() && (newVolume.intersects(this) || newVolume.intersects(other))) {
                            result.add(newVolume);
                        }

                    }
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Volume volume = (Volume) o;
            return operation == volume.operation &&
                    minX.equals(volume.minX) &&
                    maxX.equals(volume.maxX) &&
                    minY.equals(volume.minY) &&
                    maxY.equals(volume.maxY) &&
                    minZ.equals(volume.minZ) &&
                    maxZ.equals(volume.maxZ);
        }

        @Override
        public int hashCode() {
            return Objects.hash(operation, minX, maxX, minY, maxY, minZ, maxZ);
        }
    }

}
