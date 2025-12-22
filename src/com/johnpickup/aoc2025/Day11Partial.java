package com.johnpickup.aoc2025;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day11Partial {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames("2025", "Day11");
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
                nodes.forEach(n -> n.wireNodes(nodeMap));
                //Produce graphviz file
                //System.out.println(graphviz(nodeMap));
                Node startNode = nodeMap.get("you");
                Node svr = nodeMap.get("svr");
                Node out = nodeMap.get("out");
                Node dac = nodeMap.get("dac");
                Node fft = nodeMap.get("fft");

                long part1 = findPaths(startNode, out, 7).size();
                System.out.println("Part 1: " + part1);

                Node middle = nodeMap.get("oxe");  //yzf, qzf, exf, oxe
                // 79034316383680
                // 173267997409464
                // 82052969827692
                // 33224358134844

                long startFft = findPaths(svr, fft, 10).size();
                    long fftMid = findPaths(fft, middle, 10).size();
                    long midDac = findPaths(middle, dac, 10).size();
                    long dacOut = findPaths(dac, out, 10).size();
                    long part2 = startFft * fftMid * midDac * dacOut;
                    System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static String graphviz(Map<String, Node> nodeMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph \"day11\" { \n");
        sb.append("  rankdir=LR;\n");
        for (Map.Entry<String, Node> entry : nodeMap.entrySet()) {
            sb.append(String.format("  node [shape=circle] %s; %n", entry.getKey()));
            entry.getValue().outputNodes.forEach(n ->
                    sb.append(String.format("  \"%s\" -> \"%s\"; %n", entry.getKey(), n.name)));
        }
        sb.append("}");
        return sb.toString();
    }

    private static Set<List<Node>> findPaths(Node startNode, Node endNode, int maxLength) {
        if (startNode==null) return Collections.emptySet();
        if (endNode==null) return Collections.emptySet();
        // bfs
        Set<List<Node>> paths = new HashSet<>();
        Set<List<Node>> result = new HashSet<>();
        paths.add(Collections.singletonList(startNode));
        boolean madeProgress = true;
        while (madeProgress) {
            Set<List<Node>> newPaths = explore(paths, maxLength);
            Set<List<Node>> solutions = newPaths.stream().filter(p -> isSolution(p, endNode)).collect(Collectors.toSet());
            result.addAll(solutions);
            newPaths.removeAll(solutions);
            madeProgress = !paths.equals(newPaths) && !newPaths.isEmpty();
            paths = newPaths;
        }
        //System.out.printf("Found %d paths from %s to %s%n", result.size(), startNode.name, endNode.name);
        return result;
    }

    private static Set<List<Node>> explore(Set<List<Node>> paths, int maxLength) {
        Set<List<Node>> result = new HashSet<>();
        for (List<Node> path : paths) {
            Node last = path.getLast();
            for (Node outputNode : last.outputNodes) {
                if (!path.contains(outputNode) && path.size() <= maxLength) {
                    List<Node> extendedPath = new ArrayList<>(path);
                    extendedPath.add(outputNode);
                    result.add(extendedPath);
                    //System.out.println(extendedPath);
                }
            }
        }
        return result;
    }

    private static boolean isSolution(List<Node> path, Node endNode) {
        return path.contains(endNode);
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(exclude = {"outputNodes", "outputs"})
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
        @Override
        public String toString() {
            return name;
        }
    }
}
