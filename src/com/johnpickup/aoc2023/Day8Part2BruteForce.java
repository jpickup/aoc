package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fails to complete in reasonable time
 * given the answer is 12927600769609 and that we can only brute force a ten million in a second or two this is no surprise
 * as it would be > 10 days
 */
public class Day8Part2BruteForce {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day8.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            String instructions = lines.get(0);
            Map<String, Node> nodes = lines.stream()
                    .filter(s -> s.contains("="))
                    .map(Node::parse)
                    .collect(Collectors.toMap(Node::getName, s -> s));

            long instructionIdx = 0;

            List<String> locations = nodes.keySet().stream().filter(s -> s.charAt(2) == 'A').collect(Collectors.toList());

            while (locations.stream().anyMatch(s -> s.charAt(2)!='Z')) {
                char direction = instructions.charAt((int)(instructionIdx % (long)instructions.length()));
                locations = locations.stream().map(l -> {
                    switch (direction) {
                        case 'L' : return nodes.get(l).left;
                        case 'R' : return nodes.get(l).right;
                        default: throw new RuntimeException("Invalid direction");
                    }
                }).collect(Collectors.toList());

                instructionIdx++;
                if (instructionIdx%10000000 == 0) {
                    System.out.print('.');
                }
            }

            System.out.println(instructionIdx);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
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
}
