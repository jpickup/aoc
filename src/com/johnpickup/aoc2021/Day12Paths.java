package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12Paths {
    public static void main(String[] args) throws Exception {

        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day12Input.txt"))) {
            List<Edge> edges = stream.map(Day12Paths::parseInput).filter(Objects::nonNull).collect(Collectors.toList());
            List<Edge> reverseEdges = edges.stream().map(e -> new Edge(e.node2, e.node1)).collect(Collectors.toList());
            edges.addAll(reverseEdges);

            int result = 0;

            List<List<String>> paths = new ArrayList<>();
            paths.add(Collections.singletonList("start"));

            int lengthBefore, lengthAfter;
            do {
                lengthBefore = totalPathLength(paths);
                paths = addPaths(paths, edges);
                lengthAfter = totalPathLength(paths);
            }
            while (lengthBefore != lengthAfter);


            List<List<String>> completePaths = paths.stream().filter(Day12Paths::isComplete).collect(Collectors.toList());

            System.out.println("Result " + completePaths.size());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static List<List<String>> addPaths(List<List<String>> paths, List<Edge> edges) {
        List<List<String>> result = new ArrayList<>();

        for (List<String> path : paths) {
            if (isComplete(path)) {
                result.add(path);
            }
            else {
                for (Edge edge : edges) {
                    if (path.get(path.size() - 1).equals(edge.node1) &&
                            (!path.contains(edge.node2) || canRevisit(edge.node2, path)) &&
                            (!path.contains("end"))) {
                        List<String> newPath = new ArrayList<>(path);
                        newPath.add(edge.node2);
                        result.add(newPath);
                    }
                }
            }
        }
        return result;
    }

    static Edge parseInput(String s) {
        String[] strings = s.split("-");
        if (strings.length==2) return new Edge(strings[0],strings[1]);
        return null;
    }

    static int totalPathLength(List<List<String>> paths) {
        return paths.stream().map(List::size).reduce(0, Integer::sum);
    }

    static class Edge {
        String node1;
        String node2;

        public Edge(String node1, String node2) {
            this.node1 = node1;
            this.node2 = node2;
        }
    }

    static boolean canRevisit(String node, List<String> path) {
        // only upper-case nodes can be revisited
        // OR a single lower-case, once
        return node.equals(node.toUpperCase()) ||
                (!node.equals("start") && !node.equals("end") && (!pathHasSmallRevisit(path) && pathContainsOnce(path, node)));
    }

    private static boolean pathContainsOnce(List<String> path, String node) {
        return path.stream().filter(n -> n.equals(node)).count() == 1;
    }

    private static boolean pathHasSmallRevisit(List<String> path) {
        Map<String, Long> visits = path.stream().filter(n -> n.equals(n.toLowerCase())).collect(
                Collectors.groupingBy(
                        Function.identity(), Collectors.counting()
                )
        );
        return visits.values().stream().anyMatch(l -> l == 2);
    }

    static boolean isComplete(List<String> path) {
        return path.size() > 0 && path.get(path.size()-1).equals("end");
    }

}
