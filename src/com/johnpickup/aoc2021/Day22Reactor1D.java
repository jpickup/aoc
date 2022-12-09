package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day22Reactor1D {
    List<String> lines;

    public static void main(String[] args) {
        Day22Reactor1D day22 =
                new Day22Reactor1D("/Users/john/Development/AdventOfCode/resources/Day22Test.txt");

        day22.solve();
    }

    private void solve() {
        List<Instruction> instructions = lines.stream().map(this::parse).filter(Objects::nonNull).collect(Collectors.toList());
        instructions.forEach(System.out::println);

        Set<Coord> reactor = new HashSet<>();

        for (Instruction instruction : instructions) {
            executeInstruction(reactor, instruction);
        }

        long result = countCubes(reactor, -50,50,-50,50);
        System.out.println(result);
    }

    private long countCubes(Set<Coord> reactor, int minX, int maxX, int minY, int maxY) {
        return reactor.stream().filter(c -> c.inBounds(minX, maxX)).count();
    }

    private void executeInstruction(Set<Coord> reactor, Instruction instruction) {


        boolean inBounds = instruction.minX>=-50 && instruction.maxX<=50
                ;

        if (!inBounds) return;

        for (int x = instruction.minX; x <= instruction.maxX; x++) {
                    if (instruction.operation) {
                        reactor.add(new Coord(x));
                    }
                    else {
                        reactor.remove(new Coord(x));
                    }
        }
    }

    private Instruction parse(String s) {
        if (s.isEmpty()) return null;
        boolean op = s.startsWith("on");
        String rest = s.split(" ")[1];
        String[] parts = rest.split(",");

        return new Instruction(op, parseMin(parts[0]), parseMax(parts[0]));
    }

    private int parseMin(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[0]);
    }

    private int parseMax(String part) {
        return Integer.parseInt(part.substring(2).split("\\.\\.")[1]);
    }

    public Day22Reactor1D(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            lines = stream.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    static class Instruction {
        boolean operation;
        int minX, maxX;

        public Instruction(boolean operation, int minX, int maxX) {
            this.operation = operation;
            this.minX = minX;
            this.maxX = maxX;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Instruction that = (Instruction) o;
            return operation == that.operation &&
                    minX == that.minX &&
                    maxX == that.maxX
                    ;
        }

        @Override
        public int hashCode() {
            return Objects.hash(operation, minX, maxX);
        }

        @Override
        public String toString() {
            return (operation?"on":"off") + " " +
                    "  X=" + minX +
                    "..." + maxX;
        }
    }

    static class Coord implements Comparable<Coord> {
        int x;

        public static Day19Beacon.Coord parse(String line) {
            String[] split = line.split(",");
            return new Day19Beacon.Coord(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return x == coord.x;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x);
        }

        public Coord(int x) {
            this.x = x;
        }

        @Override
        public String toString() {
            return "(" +
                    x +
                    ')';
        }

        @Override
        public int compareTo(Coord o) {
            int xc = Integer.compare(x, o.x);
            return xc!=0?xc : 0;
        }

        public boolean inBounds(int minX, int maxX) {
            return (x >= minX) && (x <= maxX);
        }
    }

}
