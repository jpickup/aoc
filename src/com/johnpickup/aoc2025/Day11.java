package com.johnpickup.aoc2025;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day11 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Node> nodes = stream
                        .filter(s -> !s.isEmpty())
                        .map(Node::new)
                        .toList();

                Map<String, Node> nodeMap = nodes.stream().collect(Collectors.toMap(n -> n.name, n -> n));
                System.out.println(nodeMap);
                nodes.forEach(n -> n.wireNodes(nodeMap));
                Node startNode = nodeMap.get("you");
                Node serverNode = nodeMap.get("svr");
                Node endNode = nodeMap.get("out");

                long part1 = findPaths(startNode, endNode).size();
                System.out.println("Part 1: " + part1);

                Set<Node> part2EssentialNodes = new HashSet<>();
                part2EssentialNodes.add(nodeMap.get("dac"));
                part2EssentialNodes.add(nodeMap.get("fft"));
                long part2 = findPaths(serverNode, endNode).stream().filter(p -> p.containsAll(part2EssentialNodes)).count();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static Set<List<Node>> findPaths(Node startNode, Node endNode) {
        if (startNode==null) return Collections.emptySet();
        // bfs
        Set<List<Node>> paths = new HashSet<>();
        Set<List<Node>> result = new HashSet<>();

        paths.add(Collections.singletonList(startNode));
        boolean madeProgress = true;
        while (madeProgress) {
            Set<List<Node>> newPaths = explore(paths);
            Set<List<Node>> solutions = newPaths.stream().filter(p -> isSolution(p, endNode)).collect(Collectors.toSet());
            result.addAll(solutions);
            newPaths.removeAll(solutions);
            madeProgress = !paths.equals(newPaths) && !newPaths.isEmpty();
            paths = newPaths;
        }

        return result;
    }

    private static Set<List<Node>> explore(Set<List<Node>> paths) {
        Set<List<Node>> result = new HashSet<>();
        for (List<Node> path : paths) {
            Node last = path.getLast();
            for (Node outputNode : last.outputNodes) {
                if (!path.contains(outputNode)) {
                    List<Node> extendedPath = new ArrayList<>(path);
                    extendedPath.add(outputNode);
                    result.add(extendedPath);
                }
            }
        }
        return result;
    }

    private static boolean isSolution(List<Node> path, Node endNode) {
        return path.contains(endNode);
    }

    @Data
    @RequiredArgsConstructor
    static class Node {
        final String name;
        final List<String> outputs;
        List<Node> outputNodes;
        Node(String line) {
            String[] parts = line.split(" ");
            outputs = new ArrayList<>(Arrays.asList(parts));
            name =  outputs.removeFirst().substring(0, parts[0].length()-1);
        }
        void wireNodes(Map<String, Node> nodeMap) {
            outputNodes = outputs.stream().map(nodeMap::get).toList();
        }
    }
}
