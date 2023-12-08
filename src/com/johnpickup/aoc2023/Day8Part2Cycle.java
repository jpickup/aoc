package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8Part2Cycle {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day8.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            String instructions = lines.get(0);
            Map<String, Node> nodes = lines.stream()
                    .filter(s -> s.contains("="))
                    .map(Node::parse)
                    .collect(Collectors.toMap(Node::getName, s -> s));

            List<String> initialLocations = nodes.keySet().stream().filter(s -> s.charAt(2) == 'A').collect(Collectors.toList());
            List<Cycle> cycles = initialLocations.stream().map(l -> cycleDetails(l, instructions, nodes)).collect(Collectors.toList());

            System.out.println(cycles.stream()
                    .map(c -> c.cycleLength)
                    .map(BigInteger::valueOf)
                    .reduce(BigInteger.ONE, Day8Part2Cycle::lcm));
            // 12927600769609
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    public static BigInteger lcm(BigInteger x, BigInteger y) {
        return x.multiply(y).divide(x.gcd(y));
    }

    private static Cycle cycleDetails(String location, String instructions, Map<String, Node> nodes) {
        int instructionIdx = 0;
        String originalLocation = location;
        List<CycleNode> cycleNodes = new ArrayList<>();
        CycleNode cycleNode = new CycleNode(location, 0);
        char direction = instructions.charAt(0);

        while (!cycleNodes.contains(cycleNode)) {
            cycleNodes.add(cycleNode);
            switch (direction) {
                case 'L' : location = nodes.get(location).left; break;
                case 'R' : location = nodes.get(location).right; break;
                default: throw new RuntimeException("Invalid direction");
            }
            instructionIdx++;
            direction = instructions.charAt(instructionIdx % instructions.length());
            cycleNode = new CycleNode(location, instructionIdx % instructions.length());
        }
        long initialSteps = cycleNodes.indexOf(cycleNode);
        long cycleLength = cycleNodes.size() - initialSteps;
        List<Long> endCycleIndexes = new ArrayList<>();
        for (long i = initialSteps; i < cycleNodes.size(); i++) {
            if (cycleNodes.get((int)i).location.charAt(2)=='Z') {
                endCycleIndexes.add(i);
            }
        }
        return new Cycle(originalLocation, initialSteps, cycleLength, endCycleIndexes, cycleNodes);
    }

    @RequiredArgsConstructor
    @Data
    static class Node {
        public static Node parse(String s) {
            String[] parts1 = s.split("=");
            String[] parts2 = parts1[1].trim().split(",");
            String name = parts1[0].trim();
            String left = parts2[0].trim().replace("(", "").trim();
            String right = parts2[1].replace(")", "").trim();
            return new Node(name, left, right);
        }
        final String name;
        final String left;
        final String right;
    }

    @RequiredArgsConstructor
    @Data
    static class Cycle {
        final String start;
        final long initialSteps;
        final long cycleLength;
        final List<Long> endCycleIndexes;
        final List<CycleNode> visited;
    }

    @RequiredArgsConstructor
    @Data
    static class CycleNode {
        final String location;
        final int instructionIdx;
    }
}
