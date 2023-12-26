package com.johnpickup.aoc2023;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 {

    static Map<Integer, Integer> vertexWeights = new TreeMap<>();
    static Map<Edge, Integer> edgeImportance = new HashMap<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day25.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Map<String, Set<String>> connections = new HashMap<>();
            for (String line : lines) {
                String[] parts = line.split(":");
                String lhs = parts[0].trim();
                String[] rhss = parts[1].trim().split(" ");
                for (String rhs : rhss) {
                    connections.putIfAbsent(lhs, new HashSet<>());
                    connections.get(lhs).add(rhs);
                    connections.putIfAbsent(rhs, new HashSet<>());
                    connections.get(rhs).add(lhs);
                }
            }

            List<String> vertices = connections.keySet().stream().sorted().collect(Collectors.toList());

            int[][] adjacency = generateAdjacency(vertices, connections);
            //printAdjacency(adjacency);
            System.out.println("Graph size: " + adjacency.length);
            calcVertexWeights(adjacency);

            Set<Set<Integer>> subGraphs = generateConnectedGraphs(adjacency);
            //System.out.println(subGraphs);

            // create each graphs with 3 missing connections and see if size is 2
            List<Edge> edges = getEdges(adjacency);
            System.out.println(edges.size());

            System.out.println("Upper bound:" + 1L * edges.size() * edges.size() * edges.size());
            long progress = 0;

            edges.sort(Day25::edgeVertexComparer);

            edges.forEach(e -> System.out.printf("%s -> %d\t", e, edgeVertexWeight(e)));

            List<Edge> edgesByImportance = edgeImportance.entrySet().stream().sorted((a, b) -> -Integer.compare(a.getValue(), b.getValue())).map(Map.Entry::getKey).collect(Collectors.toList());

            System.out.printf("%nTarget: %s%n%s%n%s%n", new Edge(vertices.indexOf("mfs"), vertices.indexOf("ffv")),
                    new Edge(vertices.indexOf("mnh"), vertices.indexOf("qnv")),
                    new Edge(vertices.indexOf("ljh"), vertices.indexOf("tbg")));

            System.out.println(edgesByImportance.indexOf(new Edge(vertices.indexOf("mfs"), vertices.indexOf("ffv"))));
            System.out.println(edgesByImportance.indexOf(new Edge(vertices.indexOf("mnh"), vertices.indexOf("qnv"))));
            System.out.println(edgesByImportance.indexOf(new Edge(vertices.indexOf("ljh"), vertices.indexOf("tbg"))));

            System.out.println();

            // from visual inspection of a graphviz generated image
            int[][] newAdjacency = removeEdges(adjacency,
                    new Edge(vertices.indexOf("mfs"), vertices.indexOf("ffv")),
                    new Edge(vertices.indexOf("mnh"), vertices.indexOf("qnv")),
                    new Edge(vertices.indexOf("ljh"), vertices.indexOf("tbg"))
            );
            Set<Set<Integer>> subGraphs2 = generateConnectedGraphs(newAdjacency);
            System.out.printf("Set of %d disconnected graphs%n", subGraphs2.size());
            List<Integer> sizes = subGraphs2.stream().map(Set::size).collect(Collectors.toList());
            System.out.println(sizes);
            Long part1 = sizes.stream().map(s -> (long) s).reduce(1L, (a, b) -> a * b);
            System.out.println("Part1: " + part1);
            System.exit(0);


// brute force - will take years!
//            for (int e1=0; e1 < edges.size(); e1++) {
//                for (int e2=e1+1; e2 < edges.size(); e2++) {
//                    for (int e3=e2+1; e3 < edges.size(); e3++) {
//                        //System.out.printf("%d,%d,%d%n", edgeVertexWeight(edges.get(e1)), edgeVertexWeight(edges.get(e2)), edgeVertexWeight(edges.get(e3)));
//                        if (++progress % 100 == 0) {
//                            System.out.printf("%d in %ds%n", progress, (System.currentTimeMillis() - startTime) / 1000);
//                        }
//                        int[][] newAdjacency = removeEdges(adjacency, edges.get(e1), edges.get(e2), edges.get(e3));
//                        Set<Set<Integer>> subGraphs = generateConnectedGraphs(newAdjacency);
//                        if (subGraphs.size() != 1) {
//                            System.out.printf("Found set of %d disconnected graphs%n", subGraphs.size());
//                            List<Integer> sizes = subGraphs.stream().map(sg -> sg.size()).collect(Collectors.toList());
//                            System.out.println(sizes);
//                            Long part1 = sizes.stream().map(s -> (long) s).reduce(1L, (a,b)->a*b);
//                            System.out.println("Part1: " + part1);
//                            System.exit(0);
//                        }
//                    }
//                }
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Time: " + (endTime - startTime) + "ms");
    }

    private static void printEdges(Map<String, Set<String>> connections) {
        for (Map.Entry<String, Set<String>> entry : connections.entrySet()) {
            String v1 = entry.getKey();
            for (String v2 : entry.getValue()) {
                if (v1.compareTo(v2) < 0) {
                    System.out.printf("%s -- %s;%n", v1, v2);
                }
            }
        }
    }

    private static void calcVertexWeights(int[][] adjacency) {
        for (int i = 0; i < adjacency.length; i++) {
            int weight = 0;
            for (int j = 0; j < adjacency[i].length; j++) {
                weight += adjacency[i][j];
            }
            vertexWeights.put(i, weight);
        }
    }

    private static int edgeVertexComparer(Edge e1, Edge e2) {
        int w1 = edgeVertexWeight(e1);
        int w2 = edgeVertexWeight(e2);
        return Integer.compare(w1, w2);
    }

    private static int edgeVertexWeight(Edge e) {
        return vertexWeight(e.v1) + vertexWeight(e.v2);
    }

    private static int vertexWeight(int v) {
        return vertexWeights.get(v);
    }

    private static int[][] removeEdges(int[][] adjacency, Edge e1, Edge e2, Edge e3) {
        int[][] result = new int[adjacency.length][adjacency[0].length];
        for (int i = 0; i < adjacency.length; i++) {
            for (int j = 0; j < adjacency[i].length; j++) {
                Edge e = new Edge(i, j);
                if (!e.equals(e1) && !e.equals(e2) && !e.equals(e3)) {
                    result[i][j] = adjacency[i][j];
                }
            }
        }

        return result;
    }

    private static List<Edge> getEdges(int[][] adjacency) {
        Set<Edge> result = new HashSet<>();

        for (int i = 0; i < adjacency.length; i++) {
            for (int j = i + 1; j < adjacency[i].length; j++) {
                if (adjacency[i][j] != 0) {
                    result.add(new Edge(i, j));
                }
            }
        }
        return new ArrayList<>(result);
    }

    private static Set<Set<Integer>> generateConnectedGraphs(int[][] adjacency) {
        Set<Set<Integer>> result = new HashSet<>();

        for (int node = 0; node < adjacency.length; node++) {
            Set<Integer> connectedNodes = allConnectedNodes(adjacency, new HashSet<>(), node);
            //System.out.printf("Connected to %d: %d (%s)%n", node, connectedNodes.size(), connectedNodes);
            // fully connected
            if (connectedNodes.size() == adjacency.length) {
                return Collections.singleton(connectedNodes);
            }
            result.add(connectedNodes);
        }

        return result;
    }

    static Set<Integer> allConnectedNodes(int[][] adjacency, Set<Integer> visited, int node) {
        if (visited.contains(node)) return Collections.emptySet();
        Set<Integer> result = new TreeSet<>();
        for (int i = 0; i < adjacency.length; i++) {
            if (adjacency[node][i] == 1) {
                Edge edge = new Edge(node, i);
                edgeImportance.put(edge, edgeImportance.getOrDefault(edge, 0)+1);
                result.add(node);
                visited.add(node);
                result.addAll(allConnectedNodes(adjacency, visited, i));
            }
        }
        return result;
    }

    private static int[][] generateAdjacency(List<String> vertices, Map<String, Set<String>> connections) {
        int[][] result = new int[vertices.size()][vertices.size()];

        for (Map.Entry<String, Set<String>> entry : connections.entrySet()) {
            int idx1 = vertices.indexOf(entry.getKey());
            for (String s : entry.getValue()) {
                int idx2 = vertices.indexOf(s);
                result[idx1][idx2] = 1;
            }
        }
        return result;
    }

    private static void printAdjacency(int[][] adjacency) {
        for (int i = 0; i < adjacency.length; i++) {
            for (int j = 0; j < adjacency[i].length; j++) {
                System.out.printf("%4d", adjacency[i][j]);
            }
            System.out.println();
        }
    }

    @Data
    static class Edge {
        final int v1;
        final int v2;

        public Edge(int v1, int v2) {
            if (v1 < v2) {
                this.v1 = v1;
                this.v2 = v2;
            } else {
                this.v1 = v2;
                this.v2 = v1;
            }
        }
    }
}
