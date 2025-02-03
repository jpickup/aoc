package com.johnpickup.aoc2018;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day8 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                License license = new License(lines.get(0));
                System.out.println("Part 1: " + license.part1());
                System.out.println("Part 2: " + license.part2());
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class License {
        final List<Integer> numbers;
        Node root;

        License(String line) {
            numbers = Arrays.stream(line.split(" ")).map(Integer::parseInt).collect(Collectors.toList());
            root = Node.build(new ArrayList<>(numbers));
        }

        long part1() {
            return root.totalMetaData();
        }

        long part2() {
            return root.value();
        }
    }

    @Data
    static class Node {
        static int idSource = 0;
        final int id;
        final List<Node> children;
        final List<Integer> metadata;

        static Node build(List<Integer> input) {
            int id = idSource++;
            if (input.isEmpty()) return null;
            int numChildren = input.get(0);
            int numMetaData = input.get(1);
            input.remove(0);
            input.remove(0);
            List<Node> children = new ArrayList<>(numChildren);
            List<Integer> metadata = new ArrayList<>(numMetaData);
            for (int c = 0; c < numChildren; c++) {
                children.add(Node.build(input));
            }
            for (int m = 0; m < numMetaData; m++) {
                metadata.add(input.remove(0));
            }
            return new Node(id, children, metadata);
        }

        public long totalMetaData() {
            return metadata.stream().reduce(0, Integer::sum)
                    + children.stream().map(Node::totalMetaData).reduce(0L, Long::sum);
        }

        public long value() {
            if (children.isEmpty()) return metadata.stream().reduce(0, Integer::sum);
            return metadata.stream()
                    .filter(m -> m <= children.size())
                    .map(m -> m - 1)
                    .map(children::get)
                    .map(Node::value)
                    .reduce(0L, Long::sum);
        }
    }
}
