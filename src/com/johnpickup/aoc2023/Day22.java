package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day22 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day22.txt"))) {
            List<Brick> bricks = stream.filter(s -> !s.isEmpty()).map(Brick::parse).collect(Collectors.toList());

            List<Brick> dropped = dropAllMulti(bricks);
            System.out.printf("Time taken to drop to bottom: %ds%n", (System.currentTimeMillis() - start)/1000);

            int part1 = 0;
            int count = 0;
            int part2 = 0;
            // test each brick to see if it can be removed without others falling
            for (Brick brick : dropped) {
                int n = removesHowMany(brick, dropped);
                part2 += n;
                if (n>0) part1++;
                System.out.printf("Tested %d (%d of %d), can now remove %d%n. %d drop, so total dropping %d%n Time taken %ds%n",
                        brick.id, ++count, dropped.size(), part1, n, part2, (System.currentTimeMillis() - start)/1000);
            }
            System.out.println("Part 1: "+ part1);
            System.out.println("Part 2: "+ part2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static int howManyCanDrop(List<Brick> bricks) {
        List<Brick> sorted = new ArrayList<>(bricks);
        sorted.sort(Comparator.comparingInt(a -> a.c1.z));
        int result = 0;

        List<Brick> updated = new ArrayList<>(sorted);
        int lastProgress = 0;
        while (lastProgress < updated.size()) {
            lastProgress++;
            Brick brick = updated.remove(0);
            Set<Volume> volumes = updated.stream().map(Brick::volume).collect(Collectors.toSet());
            if (clearBelow(brick, volumes)) {
                lastProgress = 0;
                result ++;
            }
            else {
                updated.add(brick);
            }
        }
        return result;
    }

    private static List<Brick> dropAllMulti(List<Brick> bricks) {
        List<Brick> updated = new ArrayList<>(bricks);
        int insertPoint = Math.min(updated.size()-1, 20);
        updated.sort(Comparator.comparingInt(a -> a.c1.z));
        int lastProgress = 0;
        while (lastProgress < updated.size()) {
            lastProgress++;
            Brick brick = updated.remove(0);
            boolean dropped = false;
            Set<Volume> volumes = updated.stream().map(Brick::volume).collect(Collectors.toSet());
            while (clearBelow(brick, volumes)) {
                lastProgress = 0;
                dropped = true;
                brick = brick.drop();
            }
            if (dropped){
                // if this made progress come back to it sooner rather than later
                updated.add(insertPoint, brick);
            }
            else {
                updated.add(brick);
            }
        }
        return updated;
    }

    private static int removesHowMany(Brick brick, List<Brick> bricks) {
        List<Brick> without = new ArrayList<>(bricks);
        without.remove(brick);
        return howManyCanDrop(without);
    }

    private static boolean clearBelow(Brick brick, Set<Volume> volumes) {
        Brick dropped = brick.drop();
        if (dropped.c1.z == 0 || dropped.c2.z == 0) return false;
        boolean blocked = volumes.stream().anyMatch(v -> v.containsVolume(dropped.volume()));
        return !blocked;
    }

    static String visualise(List<Brick> bricks) {
        Set<Volume> volumes = bricks.stream().map(Brick::volume).collect(Collectors.toSet());
        int minX = volumes.stream().map(Volume::minX).min(Integer::compare).orElse(0);
        int maxX = volumes.stream().map(Volume::maxX).max(Integer::compare).orElse(0);
        int minY = volumes.stream().map(Volume::minY).min(Integer::compare).orElse(0);
        int maxY = volumes.stream().map(Volume::maxY).max(Integer::compare).orElse(0);
        int minZ = volumes.stream().map(Volume::minZ).min(Integer::compare).orElse(0);
        int maxZ = volumes.stream().map(Volume::maxZ).max(Integer::compare).orElse(0);

        return visualizeXZ(volumes, minX, maxX, minY, maxY, minZ, maxZ) + "\n" +
                visualizeYZ(volumes,  minX, maxX, minY, maxY, minZ, maxZ);
    }

    private static String visualizeXZ(Set<Volume> volumes, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        StringBuilder sb = new StringBuilder();
        sb.append("-x----\n");
        for (int z = maxZ; z >= 0; z--) {
            for (int x = minX; x <= maxX; x++) {
                boolean hasAnything = false;
                for (int y = minY; y <= maxY; y++) {
                    Coord coord = new Coord(x, y, z);
                    hasAnything |= volumes.stream().map(v -> v.containsCoord(coord)).reduce(false, Boolean::logicalOr);
                }
                sb.append(hasAnything?"*":(z==0)?"-":".");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String visualizeYZ(Set<Volume> volumes, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        StringBuilder sb = new StringBuilder();
        sb.append("-y----\n");
        for (int z = maxZ; z >= 0; z--) {
            for (int y = minY; y <= maxY; y++) {
                boolean hasAnything = false;
                for (int x = minX; x <= maxX; x++) {
                    Coord coord = new Coord(x, y, z);
                    hasAnything |= volumes.stream().map(v -> v.containsCoord(coord)).reduce(false, Boolean::logicalOr);
                }
                sb.append(hasAnything?"*":(z==0)?"-":".");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @RequiredArgsConstructor
    @Data
    static class Brick {
        static int seq=0;
        final int id;
        final Coord c1;
        final Coord c2;

        private Volume volume = null;

        public static Brick parse(String s) {
            String[] parts = s.split("~");
            return new Brick(++seq, Coord.parse(parts[0]), Coord.parse(parts[1]));
        }

        public Volume volume() {
            if (volume == null) {
                if (c1.x != c2.x && c1.y == c2.y && c1.z == c2.z) {
                    return new Volume(
                            IntStream.rangeClosed(Math.min(c1.x, c2.x), Math.max(c1.x, c2.x))
                                    .mapToObj(x -> new Coord(x, c1.y, c1.z)).collect(Collectors.toSet()));
                }
                if (c1.x == c2.x && c1.y != c2.y && c1.z == c2.z) {
                    return new Volume(
                            IntStream.rangeClosed(Math.min(c1.y, c2.y), Math.max(c1.y, c2.y))
                                    .mapToObj(y -> new Coord(c1.x, y, c1.z)).collect(Collectors.toSet()));
                }
                if (c1.x == c2.x && c1.y == c2.y && c1.z != c2.z) {
                    return new Volume(
                            IntStream.rangeClosed(Math.min(c1.z, c2.z), Math.max(c1.z, c2.z))
                                    .mapToObj(z -> new Coord(c1.x, c1.y, z)).collect(Collectors.toSet()));
                }
                volume = new Volume(Collections.singleton(c1));
            }
            return volume;
        }

        @Override
        public String toString() {
            return "" +
                    c1 +
                    "~" + c2;
        }

        public Brick drop() {
            return new Brick(id, c1.decZ(), c2.decZ());
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Volume {
        final Set<Coord> coords;

        // cache to improve performance
        Integer minx=null;
        Integer miny=null;
        Integer minz=null;
        Integer maxx=null;
        Integer maxy=null;
        Integer maxz=null;

        public int minX() {
            if (minx==null) minx=coords.stream().map(Coord::getX).min(Integer::compare).orElse(0);
            return minx;
        }
        public int minY() {
            if (miny==null) miny=coords.stream().map(Coord::getY).min(Integer::compare).orElse(0);
            return miny;
        }
        public int minZ() {
            if (minz==null) minz=coords.stream().map(Coord::getZ).min(Integer::compare).orElse(0);
            return minz;
        }
        public int maxX() {
            if (maxx==null) maxx=coords.stream().map(Coord::getX).max(Integer::compare).orElse(0);
            return maxx;
        }
        public int maxY() {
            if (maxy==null) maxy=coords.stream().map(Coord::getY).max(Integer::compare).orElse(0);
            return maxy;
        }
        public int maxZ() {
            if (maxz==null) maxz=coords.stream().map(Coord::getZ).max(Integer::compare).orElse(0);
            return maxz;
        }

        public boolean containsCoord(Coord coord) {
            return coords.contains(coord);
        }

        public boolean containsVolume(Volume other) {
            return other.coords.stream().anyMatch(this::containsCoord);
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;
        final int z;

        public static Coord parse(String s) {
            String[] parts = s.split(",");
            return new Coord(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }

        @Override
        public String toString() {
            return "(" +
                    x + ',' + y + ',' + z +
                    ')';
        }

        public Coord decZ() {
            return new Coord(x, y, z-1);
        }
    }

}
