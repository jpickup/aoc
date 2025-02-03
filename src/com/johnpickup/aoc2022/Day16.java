package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Day16 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day16-test.txt"))) {
            long start = System.currentTimeMillis();

            List<Node> nodes = stream.filter(s -> !s.isEmpty()).map(Day16::parse).collect(Collectors.toList());
            Map<String, Node> nodeMap = nodes.stream().collect(Collectors.toMap(n -> n.name, n -> n));
            Node root = nodeMap.get("AA");
            updateRefs(root, nodeMap, null);
            System.out.println(root);

            int time = 0;
            int maxPressure = search(root, 0, time, "");

            System.out.println(maxPressure);

            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "(ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int search(Node node, int pressure, int time, String visited) {
        System.out.println(time + ": " + node.name + " - " + visited);
        if (time >= 30) return pressure;

        int maxNoTurn = 0;
        int maxTurn = 0;
        for (Node child : node.children) {
            int visitedCount = 0;
            String v = visited;
            while (v.contains(child.name)) {v=v.substring(v.indexOf(child.name)+2); visitedCount++;}

            if (visitedCount<=2) {
                int childPressureNoTurn = search(child, pressure, time + 1, visited + node.name + "|");
                if (childPressureNoTurn > maxNoTurn) maxNoTurn = childPressureNoTurn;
            }
            if (!visited.contains(child.name + "|") && node.flowRate>0 && time<=28) {
                int childPressureWithTurn = search(child, pressure + node.flowRate * (30 - time), time + 2, visited + node.name + "|");
                if (childPressureWithTurn > maxTurn) maxTurn = childPressureWithTurn;
            }
        }
        return pressure + Math.max(maxNoTurn, maxTurn);
    }

    private static void updateRefs(Node node, Map<String, Node> nodeMap, Node parent) {
        if (node.parent == null) {
            node.parent = parent;
            for (String childName : node.childNames) {
                Node child = nodeMap.get(childName);
                if (child == null) throw new RuntimeException("Failed to find " + childName);
                node.children.add(child);
                updateRefs(child, nodeMap, node);
            }
        }
    }

    private static Node parse(String s) {
        // Valve AA has flow rate=0; tunnels lead to valves DD, II, BB
        String name = s.split(" ")[1];
        int flowRate = Integer.parseInt(s.substring(s.indexOf("=") + 1, s.indexOf(";")));
        List<String> children = Arrays.stream(s.substring(s.indexOf("valve") + "valves".length()).split(",")).map(String::trim).collect(Collectors.toList());
        return Node.builder()
                .name(name)
                .flowRate(flowRate)
                .childNames(children)
                .build();
    }

    @Data
    @Builder
    @ToString(exclude = {"parent","children"})
    static class Node {
        final String name;
        final int flowRate;
        final List<String> childNames;
        Node parent;
        final List<Node> children = new ArrayList<>();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(name, node.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

}
