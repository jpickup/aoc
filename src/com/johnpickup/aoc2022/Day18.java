package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day18 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day18.txt"))) {
            long start = System.currentTimeMillis();
            Set<Cube> cubes = stream.filter(s -> !s.isEmpty()).map(Cube::parse).collect(Collectors.toSet());

            // part 1
            int cubeSideCount = cubes.size() * 6;
            int commonSides = countCommonSides(cubes);
            int result1 = cubeSideCount - (commonSides * 2);
            System.out.println("Part 1 result=" + result1);

            // part 2 - 4184 is too high
//            int internalCubes = countInternalCubes(cubes);
//            int result2 = cubeSideCount - (commonSides * 2) - internalCubes * 6;
//            System.out.println("Part 2 result=" + result2);

            // Part 2 (recursive)
            Cube first = cubes.stream().findFirst().get();

            int result2 = discoverCubes(first, new HashSet<>(cubes), new HashSet<>());
            System.out.println("Part 2 result=" + result2);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static int discoverCubes(Cube from, Set<Cube> remain, Set<Cube> found) {
        if (remain.isEmpty()) return 0;

        Set<Cube> connected = new HashSet<>();
        for (Cube r : remain) {
            if (from.sharesSide(r)) {
                connected.add(r);
            }
        }


        int numConnected = connected.size();
        for (Cube conn : connected) {
            found.add(conn);
            remain.remove(conn);
            numConnected += discoverCubes(conn, remain, found);
        }

        return 1 + numConnected;
    }

    private static int countInternalCubes(Set<Cube> cubes) {
        int result = 0;

        int minX = cubes.stream().map(c -> c.x).min(Integer::compareTo).get();
        int maxX = cubes.stream().map(c -> c.x).max(Integer::compareTo).get();
        int minY = cubes.stream().map(c -> c.y).min(Integer::compareTo).get();
        int maxY = cubes.stream().map(c -> c.y).max(Integer::compareTo).get();
        int minZ = cubes.stream().map(c -> c.z).min(Integer::compareTo).get();
        int maxZ = cubes.stream().map(c -> c.z).max(Integer::compareTo).get();

        for (int x = minX+1; x < maxX; x++) {
            for (int y = minY+1; y < maxY; y++) {
                for (int z = minZ+1; z < maxZ; z++) {
                    Cube space = Cube.builder().x(x).y(y).z(z).build();
                    if (cubes.contains(space)) continue;
                    int sharedSides=0;
                    for (Cube cube : cubes) {
                        if (cube.sharesSide(space)) sharedSides++;
                    }
                    if (sharedSides == 6) {
                        result++;
                    }
                }
            }
        }

        return result;
    }

    private static int countCommonSides(Set<Cube> cubes) {
        int result = 0;
        for (Cube cube1 : cubes) {
            for (Cube cube2 : cubes) {
                if (cube1.compareTo(cube2) <= 0) continue;
                if (cube1.sharesSide(cube2)) result++;
            }
        }

        return result;
    }

    @Builder
    @EqualsAndHashCode
    @ToString
    static class Cube implements Comparable<Cube> {
        final int x;
        final int y;
        final int z;

        public static Cube parse(String s) {
            String[] parts = s.split(",");
            return Cube.builder()
                    .x(Integer.parseInt(parts[0]))
                    .y(Integer.parseInt(parts[1]))
                    .z(Integer.parseInt(parts[2]))
                    .build();
        }

        public boolean sharesSide(Cube other) {
            return sharesXSide(other) || sharesYSide(other) || sharesZSide(other);
        }

        boolean sharesXSide(Cube other) {
            return (this.x==other.x-1 || this.x==other.x+1) && this.y==other.y && this.z==other.z;
        }

        boolean sharesYSide(Cube other) {
            return this.x==other.x && (this.y==other.y-1 || this.y==other.y+1) && this.z==other.z;
        }

        boolean sharesZSide(Cube other) {
            return this.x==other.x && this.y==other.y && (this.z==other.z-1 || this.z==other.z+1);
        }

        @Override
        public int compareTo(Cube o) {
            if (this.x != o.x) return this.x - o.x;
            if (this.y != o.y) return this.y - o.y;
            return this.z - o.z;
        }
    }
}
