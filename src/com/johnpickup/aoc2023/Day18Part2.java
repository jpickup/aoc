package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18Part2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day18.txt"))) {
            List<Instruction> instructions = stream.filter(s -> !s.isEmpty()).map(Instruction::parse).collect(Collectors.toList());

            Ground ground = new Ground();
            ground.process(instructions);
            System.out.println(ground.part2());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class Ground {
        private final List<Coord> dug = new ArrayList<>();

        void dig(Coord coord) {
            dug.add(coord);
        }

        public BigInteger part2() {
            BigInteger s1 = BigInteger.ZERO;
            BigInteger s2 = BigInteger.ZERO;
            BigInteger lengths = BigInteger.ZERO;

            for (int i = 0; i < dug.size(); i++) {
                Coord coord1 = dug.get(i);
                Coord coord2 = i==dug.size()-1 ? dug.get(0) : dug.get(i+1);
                s1 = s1.add(BigInteger.valueOf(coord1.x).multiply(BigInteger.valueOf(coord2.y)));
                s2 = s2.add(BigInteger.valueOf(coord1.y).multiply(BigInteger.valueOf(coord2.x)));
                lengths = lengths.add(BigInteger.valueOf(coord1.x).subtract(BigInteger.valueOf(coord2.x)).abs())
                        .add(BigInteger.valueOf(coord1.y).subtract(BigInteger.valueOf(coord2.y)).abs());
            }

            return s1.subtract(s2).add(lengths).abs().divide(BigInteger.valueOf(2)).add(BigInteger.ONE);
        }

        public void process(List<Instruction> instructions) {
            Coord location = new Coord(0,0);
            dig(location);
            for (Instruction instruction : instructions) {
                location = process(location, instruction);
            }
        }

        private Coord process(Coord location, Instruction instruction) {
            location = location.move(instruction.direction, instruction.distance);
            dig(location);
            return location;
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
            String colour = parts[2].substring(1, parts[2].length() - 1);
            // part 2
            long distance = Long.decode("0x" + colour.substring(1,6));

            Direction direction;
            switch (colour.charAt(6)) {
                case '0' : direction = Direction.RIGHT; break;
                case '1' : direction = Direction.DOWN; break;
                case '2' : direction = Direction.LEFT; break;
                case '3' : direction = Direction.UP; break;
                default: throw new RuntimeException("Unknown direction " + colour.charAt(5));
            }

            return new Instruction(direction,
                    (int)distance,
                    colour);
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
}
