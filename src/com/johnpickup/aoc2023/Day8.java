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

public class Day8 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day8.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            String instructions = lines.get(0);
            Map<String, Node> nodes = lines.stream()
                    .filter(s -> s.contains("="))
                    .map(Node::parse)
                    .collect(Collectors.toMap(Node::getName, s -> s));

            String location = "AAA";
            int instructionIdx = 0;

            while (!location.equals("ZZZ")) {
                char direction = instructions.charAt(instructionIdx % instructions.length());
                System.out.println(location + ' ' + direction);
                switch (direction) {
                    case 'L' : location = nodes.get(location).left; break;
                    case 'R' : location = nodes.get(location).right; break;
                    default: throw new RuntimeException("Invalid direction");
                }
                instructionIdx++;
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
