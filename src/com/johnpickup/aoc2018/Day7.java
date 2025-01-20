package com.johnpickup.aoc2018;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day7 {
    static boolean isTest;
    static int base_duration;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2018/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Order> orders = stream
                        .filter(s -> !s.isEmpty())
                        .map(Order::new)
                        .collect(Collectors.toList());
                Set<Node> nodes = new HashSet<>();
                nodes.addAll(orders.stream().map(Order::getBefore).map(Node::new).collect(Collectors.toSet()));
                nodes.addAll(orders.stream().map(Order::getAfter).map(Node::new).collect(Collectors.toSet()));
                Map<String, Node> nodeByName = nodes.stream().collect(Collectors.toMap(n -> n.name, n -> n));
                orders.forEach(o -> nodeByName.get(o.after).addDependency(nodeByName.get(o.before)));

                List<Node> completed = new ArrayList<>();
                while (completed.size() < nodes.size()) {
                    List<Node> satisfied = nodes.stream()
                            .filter(n -> !completed.contains(n))
                            .filter(n -> n.isSatisfiedBy(completed))
                            .sorted(Comparator.comparing(a -> a.name))
                            .collect(Collectors.toList());
                    completed.add(satisfied.get(0));
                }
                System.out.println("Part 1: " + completed.stream().map(n -> n.name).collect(Collectors.joining()));

                base_duration = isTest ? 0 : 60;
                int numberOfWorkers = isTest ? 2 : 5;

                completed.clear();
                List<Node> rootNodes = nodes.stream()
                        .filter(n -> n.isSatisfiedBy(completed))
                        .sorted(Comparator.comparing(a -> a.name))
                        .collect(Collectors.toList());
                rootNodes.forEach(n -> n.setStartTime(0));
                int time = 0;
                Set<Node> inProgress = new HashSet<>();
                inProgress.addAll(rootNodes);
                while (completed.size() < nodes.size()) {
                    final int now = time;
                    List<Node> satisfied = inProgress.stream()
                            .filter(n -> !completed.contains(n))
                            .filter(n -> n.isSatisfiedBy(completed))
                            .filter(n -> n.isCompletedBy(now))
                            .sorted(Comparator.comparing(a -> a.name))
                            .collect(Collectors.toList());
                    completed.addAll(satisfied);
                    inProgress.removeAll(satisfied);
                    List<Node> candidates = nodes.stream()
                            .filter(n -> !completed.contains(n))
                            .filter(n -> !inProgress.contains(n))
                            .filter(n -> n.isSatisfiedBy(completed))
                            .sorted(Comparator.comparing(a -> a.name))
                            .limit(numberOfWorkers - inProgress.size())
                            .collect(Collectors.toList());
                    candidates.forEach(n -> n.setStartTime(now));
                    inProgress.addAll(candidates);
                    // next time is the minimum of the completion times of in-progress items
                    if (completed.size() < nodes.size()) {
                        time = inProgress.stream()
                                .map(Node::completeTime).min(Integer::compareTo)
                                .orElseThrow(() -> new RuntimeException("Nothing left to complete"));
                    }
                }
                System.out.println("Part 2: " + time);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @Data
    @EqualsAndHashCode(exclude = {"dependsOn","startTime"})
    static class Node {
        final String name;
        final List<Node> dependsOn = new ArrayList<>();
        int startTime = -1;

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int completeTime() {
            return startTime + base_duration + (name.charAt(0)-'A' + 1);
        }

        @Override
        public String toString() {
            return name + " " + dependsOn;
        }

        public void addDependency(Node node) {
            dependsOn.add(node);
        }

        public boolean isSatisfiedBy(Collection<Node> nodes) {
            return nodes.containsAll(dependsOn);
        }

        public boolean isCompletedBy(int time) {
            return time >= completeTime();
        }
    }

    @Data
    static class Order {
        static final Pattern pattern = Pattern.compile("Step (.) must be finished before step (.) can begin\\.");
        final String before;
        final String after;

        Order(String line) {
            Matcher matcher = pattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException("Invalid input " + line);
            before = matcher.group(1);
            after = matcher.group(2);
        }
    }
}
