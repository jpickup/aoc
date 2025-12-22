package com.johnpickup.aoc2025;

import com.johnpickup.util.Pair;
import lombok.EqualsAndHashCode;
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

                long part1 = findPaths(startNode, out).size();
                System.out.println("Part 1: " + part1);

                if (isTest) {
                    long startDac = findPaths(svr, dac).size();
                    long startFft = findPaths(svr, fft).size();
                    long dacFft = findPaths(dac, fft).size();
                    long fftDac = findPaths(fft, dac).size();
                    long fftOut = findPaths(fft, out).size();
                    long dacOut = findPaths(dac, out).size();
                    long part2 = startDac * dacFft * fftOut + startFft * fftDac * dacOut;
                    System.out.println("Part 2: " + part2);
                } else {
                    List<List<String>> stages = new ArrayList<>();
                    // visual inspection of the graph shows these key stages
                    stages.add(Collections.singletonList("svr"));
                    stages.add(Arrays.asList("cno", "afb", "bya", "mfc", "yag"));
                    stages.add(Collections.singletonList("fft"));       // add an intermediate stage to go through fft
                    stages.add(Arrays.asList("vrb", "myd", "fkk"));
                    stages.add(Arrays.asList("yzf", "qzf", "exf", "oxe"));
                    stages.add(Arrays.asList("weg", "kso", "dgp"));
                    stages.add(Collections.singletonList("dac"));       // add an intermediate stage to go through dac
                    stages.add(Arrays.asList("gbc", "hkx", "lpt", "you"));
                    stages.add(Collections.singletonList("out"));

                    List<String> prevStage = null;
                    Map<Pair<String, String>, Long> stageCounts = new HashMap<>();

                    for (List<String> stage : stages) {
                        if (prevStage != null) {
                            for (String previous : prevStage) {
                                for (String next : stage) {
                                    System.out.printf("Paths from %s to %s: ", previous, next);
                                    Set<List<Node>> paths = findPaths(nodeMap.get(previous), nodeMap.get(next));
                                    System.out.printf("%d paths%n", paths.size());

                                    stageCounts.put(new Pair<>(previous, next), (long) paths.size());
                                }
                            }
                        }
                        prevStage = stage;
                    }
                    // multiply the combinations together
                    // If we have groups A -> B,C | B,C -> D,E | D,E -> F
                    // then we need:
                    // (A->B * B->D * D->F) + (A->C * C->D * D->F) +
                    // (A->B * B->E * E->F) + (A->C * C->E * E->F)
                    prevStage = null;
                    Map<String, Long> pathCountToNode = new HashMap<>();
                    pathCountToNode.put("svr", 1L);
                    for (List<String> stage : stages) {
                        if (prevStage != null) {
                            for (String previous : prevStage) {
                                for (String next : stage) {
                                    Long stageCount = stageCounts.get(new Pair<>(previous, next));
                                    Long prevPathCount = pathCountToNode.get(previous);
                                    Long nextPathCount = pathCountToNode.getOrDefault(next, 0L) + prevPathCount * stageCount;
                                    pathCountToNode.put(next, nextPathCount);
                                }
                            }
                        }
                        prevStage = stage;
                    }
                    System.out.println("Part 2: " + pathCountToNode.get("out"));
                }
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

    private static Set<List<Node>> findPaths(Node startNode, Node endNode) {
        if (startNode==null) return Collections.emptySet();
        if (endNode==null) return Collections.emptySet();
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
        //System.out.printf("Found %d paths from %s to %s%n", result.size(), startNode.name, endNode.name);
        return result;
    }

    private static final int MAX_LENGTH = 8;

    private static Set<List<Node>> explore(Set<List<Node>> paths) {
        Set<List<Node>> result = new HashSet<>();
        for (List<Node> path : paths) {
            Node last = path.getLast();
            for (Node outputNode : last.outputNodes) {
                if (!path.contains(outputNode) && path.size() <= MAX_LENGTH) {
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
