package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19Beacon {
    List<String> lines;

    public static void main(String[] args) {
        Day19Beacon day19 =
                new Day19Beacon("/Volumes/Users/john/Development/AdventOfCode/resources/Day19Input.txt");
                //new com.johnpickup.aoc2021.Day19Beacon("/Volumes/Users/john/Development/AdventOfCode/resources/Day19Input.txt");

        day19.solve();
    }

    private void solve() {
        List<Scanner> scanners = parse(lines);
        scanners.forEach(s -> System.out.println(s.toString()));

        List<Transform> transforms = new ArrayList<>();

        for (int i = 0; i < scanners.size(); i++) {
            for (int j = 0; j < scanners.size(); j++) {
                if (i == j) continue;

                Scanner scanner1 = scanners.get(i);
                Scanner scanner2 = scanners.get(j);

                for (int rot = 0; rot < 24; rot++) {
                    final int rotf = rot;
                    List<Coord> s2rotated = scanner2.beacons.stream().map(c -> rotate(c, rotf)).collect(Collectors.toList());

                    Map<Coord, Integer> distances = new HashMap<>();
                    for (int b1 = 0; b1 < scanner1.beacons.size(); b1++) {
                        for (int b2 = 0; b2 < s2rotated.size(); b2++) {
                            CoordPair pair = new CoordPair(scanner1.beacons.get(b1), s2rotated.get(b2));
                            Coord dist = toDistance(pair);
                            if (!distances.containsKey(dist)) distances.put(dist, 1);
                            else distances.put(dist, distances.get(dist) + 1);
                        }
                    }

                    // solution found
                    if (distances.containsValue(12)) {
                        Transform transform = new Transform(i, j, rot, distances.entrySet().stream()
                                .filter(e -> e.getValue().equals(12))
                                .map(Map.Entry::getKey)
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("not found")));

                        System.out.println(transform);
                        transforms.add(transform);
                    }
                }
            }
        }

        // brute force all the paths
        for (int iter = 0; iter < scanners.size(); iter++) {
            for (Transform transform : transforms) {
                Scanner scanner1 = scanners.get(transform.source);
                Scanner scanner2 = scanners.get(transform.target);
                scanner1.fullBeacons.addAll(scanner2.fullBeacons.stream()
                        .map(c -> rotate(c, transform.rot))
                        .map(c -> translate(c, transform.offset))
                        .collect(Collectors.toList()));
                scanner1.scanners.addAll(scanner2.scanners.stream()
                        .map(c -> rotate(c, transform.rot))
                        .map(c -> translate(c, transform.offset))
                        .collect(Collectors.toList()));
            }
        }

        for (Scanner scanner : scanners) {
            System.out.printf("Scanner[%d] has %d beacons\n", scanner.id, scanner.fullBeacons.size());
            System.out.printf("Scanner[%d] has %d scanners\n", scanner.id, scanner.scanners.size());
        }

        Set<Coord> scannerLocations = scanners.get(0).scanners;
        int maxDist = 0;
        for (Coord location1 : scannerLocations) {
            for (Coord location2 : scannerLocations) {
                int d = toManhattanDistance(new CoordPair(location1, location2));
                if (d > maxDist) maxDist = d;
            }
        }
        System.out.println("Max dist:" + maxDist);
    }

    private Coord translate(Coord c, Coord translation) {
        return new Coord(c.x + translation.x, c.y + translation.y, c.z + translation.z);
    }

    static Stream<CoordPair> zip(Stream<Coord> as, Stream<Coord> bs) {
        Iterator<Coord> i=as.iterator();
        return bs.filter(x->i.hasNext()).map(b->new CoordPair(i.next(), b));
    }

    private Coord toDistance(CoordPair coordPair) {
        return new Coord(
                coordPair.c1.x - coordPair.c2.x,
                coordPair.c1.y - coordPair.c2.y,
                coordPair.c1.z - coordPair.c2.z);
    }

    private int toManhattanDistance(CoordPair coordPair) {
        return Math.abs(coordPair.c1.x - coordPair.c2.x) +
                Math.abs(coordPair.c1.y - coordPair.c2.y) +
                Math.abs(coordPair.c1.z - coordPair.c2.z);
    }


    private List<Scanner> parse(List<String> lines) {
        List<Scanner> result = new ArrayList<>();
        List<String> subLines = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("---")) {
                subLines.add(line);
            }
            else if(line.isEmpty()) {
                result.add(Scanner.parse(subLines));
                subLines.clear();
            }
            else {
                subLines.add(line);
            }
        }

        if (!subLines.isEmpty()) result.add(Scanner.parse(subLines));

        return result;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private Coord rotate(Coord c, int rotationIndex) {
        switch (rotationIndex) {
            case 0 : return new Coord(c.x, c.y, c.z);
            case 1 : return new Coord(c.x, -c.z, c.y);
            case 2 : return new Coord(c.x, -c.y, -c.z);
            case 3 : return new Coord(c.x, c.z, -c.y);

            case 4 : return new Coord(-c.x, -c.y, c.z);
            case 5 : return new Coord(-c.x, c.z, c.y);
            case 6 : return new Coord(-c.x, c.y, -c.z);
            case 7 : return new Coord(-c.x, -c.z, -c.y);

            case 8 : return new Coord(c.y, c.z, c.x);
            case 9 : return new Coord(c.y, -c.x, c.z);
            case 10 : return new Coord(c.y, -c.z, -c.x);
            case 11 : return new Coord(c.y, c.x, -c.z);

            case 12 : return new Coord(-c.y, -c.z, c.x);
            case 13 : return new Coord(-c.y, c.x, c.z);
            case 14 : return new Coord(-c.y, c.z, -c.x);
            case 15 : return new Coord(-c.y, -c.x, -c.z);

            case 16 : return new Coord(c.z, c.x, c.y);
            case 17 : return new Coord(c.z, -c.y, c.x);
            case 18 : return new Coord(c.z, -c.x, -c.y);
            case 19 : return new Coord(c.z, c.y, -c.x);

            case 20 : return new Coord(-c.z, -c.x, c.y);
            case 21 : return new Coord(-c.z, c.y, c.x);
            case 22 : return new Coord(-c.z, c.x, -c.y);
            case 23 : return new Coord(-c.z, -c.y, -c.x);
        }
        throw new RuntimeException("Unknown rotation index " + rotationIndex);
    }

    Day19Beacon(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Scanner {
        int id;
        List<Coord> beacons = new ArrayList<>();
        Set<Coord> fullBeacons = new HashSet<>();
        Set<Coord> scanners = new HashSet<>();
        static Scanner parse(List<String> lines) {
            Scanner result = new Scanner();

            result.id=Integer.parseInt(lines.get(0).split(" ")[2]);
            for (int i =1; i < lines.size(); i++) {
                result.beacons.add(Coord.parse(lines.get(i)));
                result.fullBeacons.add(Coord.parse(lines.get(i)));
            }
            result.scanners.add(new Coord(0,0,0));

            return result;
        }

        @Override
        public String toString() {
            return "Scanner{" +
                    "id=" + id +
                    ", beacons=" + beacons +
                    '}';
        }
    }

    static class Coord implements Comparable<Coord> {
        int x;
        int y;
        int z;

        public static Coord parse(String line) {
            String[] split = line.split(",");
            return new Coord(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return x == coord.x &&
                    y == coord.y &&
                    z == coord.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        public Coord(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public String toString() {
            return "(" +
                    x + "," + y +
                    "," + z +
                    ')';
        }

        @Override
        public int compareTo(Coord o) {
            int xc = Integer.compare(x, o.x);
            int yc = Integer.compare(y, o.y);
            int zc = Integer.compare(z, o.z);
            return xc!=0?xc : yc!=0?yc : zc;
        }
    }

    static class CoordPair {
        Coord c1, c2;

        public CoordPair(Coord c1, Coord c2) {
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        public String toString() {
            return "CoordPair{" +
                    "c1=" + c1 +
                    ", c2=" + c2 +
                    '}';
        }
    }

    static class Transform {
        int source;
        int target;
        int rot;
        Coord offset;

        public Transform(int source, int target, int rot, Coord offset) {
            this.source = source;
            this.target = target;
            this.rot = rot;
            this.offset = offset;
        }

        @Override
        public String toString() {
            return "Transform{" +
                    "source=" + source +
                    ", target=" + target +
                    ", rot=" + rot +
                    ", offset=" + offset +
                    '}';
        }
    }

}
