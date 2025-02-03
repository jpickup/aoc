package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day14 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day14.txt"))) {
            List<Path> paths = stream.map(Path::parse).collect(Collectors.toList());
            Cave cave = Cave.createFrom(paths);
            Coord startPoint = Coord.builder().x(500).y(0).build();

            boolean canAdd = true;
            while (canAdd) {
                canAdd = cave.addSand(startPoint) && cave.getLocation(startPoint).equals(Location.AIR);
            }

            cave.print();
            int result = cave.sandCount();
            System.out.println("Sand count: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Builder
    @ToString
    static class Cave {
        static boolean part2 = true;
        int clock = 0;
        final int minX, maxX, minY, maxY;
        final Location[][] locations;

        Location getLocation(Coord coord) {
            if ((coord.x<minX) || (coord.x>maxX) || (coord.y<minY) || (coord.y>maxY)) return null;
            return locations[coord.y - minY][coord.x - minX];
        }

        void setLocation(Coord coord, Location location) {
            if ((coord.x<minX) || (coord.x>maxX) || (coord.y<minY) || (coord.y>maxY)) throw new IndexOutOfBoundsException("Coordinate outside cave range " + coord);
            locations[coord.y - minY][coord.x - minX] = location;
        }

        static Cave createFrom(List<Path> paths) {
            int minX = 500, maxX = 500, minY = 0, maxY = 0;
            for (Path path : paths) {
                for (Coord point : path.points) {
                    if (point.x < minX) minX = point.x;
                    if (point.y < minY) minY = point.y;
                    if (point.x > maxX) maxX = point.x;
                    if (point.y > maxY) maxY = point.y;
                }
            }

            if (part2) {
                // part 2 - expand the bounds
                maxY += 2;
                maxX += maxY;
                minX -= maxY;
            }

            Cave result = Cave.builder()
                    .minX(minX)
                    .maxX(maxX)
                    .minY(minY)
                    .maxY(maxY)
                    .locations(new Location[maxY - minY + 1][maxX - minX + 1])
                    .build()
                    .init();

            if (part2) {
                // part 2 - fill in the floor
                for (int x = minX; x <= maxX; x++) {
                    result.setLocation(Coord.builder().x(x).y(maxY).build(), Location.ROCK);
                }
            }

            for (Path path : paths) {
                for (int i=0; i<path.points.size()-1; i++) {
                    Coord position = path.points.get(i);
                    Coord endPosition = path.points.get(i + 1);
                    while (!position.equals(endPosition)) {
                        result.setLocation(position, Location.ROCK);
                        position = position.moveTo(endPosition);
                    }
                    result.setLocation(endPosition, Location.ROCK);
                }
            }
            return result;
        }

        Cave init() {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    locations[y-minY][x-minX] = Location.AIR;
                }
            }
            return this;
        }

        void print() {
            System.out.println("CLOCK: " + clock);
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    System.out.print(locations[y-minY][x-minX].display);
                }
                System.out.println();
            }
        }


        boolean addSand(Coord startPoint) {
            Coord prev;
            Coord next = startPoint;
            do {
                prev = next;
                next = moveDown(prev);
                clock++;
            } while ((next != null) && (!prev.equals(next)));

            if (next != null) {
                setLocation(next, Location.SAND);
            }

            return next != null;
        }

        private Coord moveDown(Coord c) {
            Coord below = Coord.builder()
                    .x(c.x)
                    .y(c.y+1)
                    .build();
            Coord belowLeft = Coord.builder()
                    .x(c.x-1)
                    .y(c.y+1)
                    .build();
            Coord belowRight = Coord.builder()
                    .x(c.x+1)
                    .y(c.y+1)
                    .build();

            if (getLocation(below) == null) return null;
            if (getLocation(below).equals(Location.AIR)) return below;
            if (getLocation(belowLeft) == null) return null;
            if (getLocation(belowLeft).equals(Location.AIR)) return belowLeft;
            if (getLocation(belowRight) == null) return null;
            if (getLocation(belowRight).equals(Location.AIR)) return belowRight;
            return c;
        }

        int sandCount() {
            int result = 0;
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    if (locations[y-minY][x-minX] == Location.SAND) result ++;
                }
            }
            return result;
        }
    }

    @RequiredArgsConstructor
    enum Location {
        AIR('.'),
        ROCK('#'),
        SAND('o');
        final char display;
    }

    @Builder
    @ToString
    static class Path {
        final List<Coord> points;

        static Path parse(String s) {
            String[] split = s.split("->");
            List<Coord> coords = Arrays.stream(split).map(Coord::parse).collect(Collectors.toList());
            return Path.builder()
                    .points(coords)
                    .build();
        }
    }

    @Builder
    @EqualsAndHashCode
    static class Coord {
        final int x;
        final int y;

        static Coord parse(String s) {
            String[] parts = s.trim().split(",");
            return Coord.builder()
                    .x(Integer.parseInt(parts[0]))
                    .y(Integer.parseInt(parts[1]))
                    .build();
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

        public Coord moveTo(Coord p) {
            return Coord.builder()
                    .x(x + (int)Math.signum(p.x - x))
                    .y(y + (int)Math.signum(p.y - y))
                    .build();
        }
    }
}
