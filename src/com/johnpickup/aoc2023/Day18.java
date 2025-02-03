package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day18.txt"))) {
            List<Instruction> instructions = stream.filter(s -> !s.isEmpty()).map(Instruction::parse).collect(Collectors.toList());

            Ground ground = new Ground();
            ground.process(instructions);
            System.out.println(ground);

            System.out.println(ground.part1());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class Ground {
        private final Set<Coord> dug = new HashSet<>();

        void dig(Coord coord) {
            dug.add(coord);
        }

        public int part1() {
            StringBuffer sb = new StringBuffer();
            int dugCount = 0;
            int minX = minX();
            int minY = minY();
            for (int y = minY; y <= maxY(); y++) {
                int edgeCount = 0;
                for (int x = minX; x <= maxX(); x++) {
                    Coord coord = new Coord(x, y);

                    boolean isDug = dug.contains(coord);
                    boolean prevIsDug = dug.contains(new Coord(x-1, y));
                    boolean nextIsDug = dug.contains(new Coord(x+1, y));
                    boolean aboveIsDug = dug.contains(new Coord(x, y-1));
                    boolean belowIsDug = dug.contains(new Coord(x, y+1));

                    Edge edge=null;
                    if (isDug) {
                        edge = determineEdge(aboveIsDug, belowIsDug, nextIsDug, prevIsDug);
                        if (edge.equals(Edge.VERTICAL) || edge.equals(Edge.NORTH_EAST) || edge.equals(Edge.NORTH_WEST)) {
                            edgeCount++;
                        }
                    }

                    if (isDug || edgeCount%2==1) {
                        dugCount++;
                        if (isDug)
                            sb.append(edge.symbol);
                        else
                            sb.append('#');
                    } else {
                        sb.append('.');
                    }
                }
                sb.append('\n');
            }
            System.out.println(sb);
            return dugCount;
        }
        private Edge determineEdge(boolean north, boolean south, boolean east, boolean west) {
            if (north && south) return Edge.VERTICAL;
            if (east && west) return Edge.HORIZONTAL;
            if (north && east) return Edge.NORTH_EAST;
            if (north && west) return Edge.NORTH_WEST;
            if (south && east) return Edge.SOUTH_EAST;
            if (south && west) return Edge.SOUTH_WEST;
            throw new RuntimeException("Can't determine pipe");
        }

        public void process(List<Instruction> instructions) {
            Coord location = new Coord(0,0);
            dig(location);
            for (Instruction instruction : instructions) {
                location = process(location, instruction);
            }
        }

        private Coord process(Coord location, Instruction instruction) {
            for (int i = 0; i < instruction.distance; i++) {
                location = location.move(instruction.direction, 1);
                dig(location);
            }
            return location;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = minY(); y <= maxY(); y++) {
                for (int x = minX(); x <= maxX(); x++) {
                    sb.append(dug.contains(new Coord(x,y)) ? '#' : '.');
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        int minX() {
            return dug.stream().map(c -> c.x).min(Integer::compareTo).orElse(0);
        }
        int maxX() {
            return dug.stream().map(c -> c.x).max(Integer::compareTo).orElse(0);
        }

        int minY() {
            return dug.stream().map(c -> c.y).min(Integer::compareTo).orElse(0);
        }
        int maxY() {
            return dug.stream().map(c -> c.y).max(Integer::compareTo).orElse(0);
        }
    }


    @RequiredArgsConstructor
    @Data
    static class Instruction {
        private final Direction direction;
        private final int distance;
        private final String colour;
        public static Instruction parse(String s) {
            String[] parts = s.split(" ");
            return new Instruction(Direction.parse(parts[0].charAt(0)),
                    Integer.parseInt(parts[1]),
                    parts[2].substring(1, parts[2].length()-1));
        }
    }

    @RequiredArgsConstructor
    @Data
    static class Coord {
        final int x;
        final int y;

        public Coord move(Direction direction, int distance) {
            switch (direction) {
                case UP: return this.up(distance);
                case RIGHT: return this.right(distance);
                case LEFT: return this.left(distance);
                case DOWN: return this.down(distance);
                default: throw new RuntimeException("Unknown direction");
            }
        }

        public Coord right(int distance) {
            return new Coord(x+distance, y);
        }
        public Coord up(int distance) {
            return new Coord(x, y-distance);
        }
        public Coord down(int distance) {
            return new Coord(x, y+distance);
        }
        public Coord left(int distance) {
            return new Coord(x-distance, y);
        }
    }

    @RequiredArgsConstructor
    enum Direction {
        UP('U'),
        DOWN('D'),
        LEFT('L'),
        RIGHT('R');
        final char ch;

        public static Direction parse(char ch) {
            switch(ch) {
                case 'U': return UP;
                case 'D': return DOWN;
                case 'L': return LEFT;
                case 'R': return RIGHT;
                default: throw new RuntimeException("Unknown direction " + ch);
            }
        }
    }

    @RequiredArgsConstructor
    enum Edge {
        VERTICAL('|'),
        HORIZONTAL('-'),
        NORTH_EAST('L'),
        NORTH_WEST('J'),
        SOUTH_EAST('F'),
        SOUTH_WEST('7'),
        GROUND('.'),
        START('S');
        final char symbol;
    }
}
