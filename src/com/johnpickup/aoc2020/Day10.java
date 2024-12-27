package com.johnpickup.aoc2020;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {
    public static void main(String[] args) {
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day10/Day10";
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test-small.txt"
                , prefix + "-test.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Integer> lines = stream
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                Adapters adapters = new Adapters(lines);
                int part1 = adapters.part1();
                System.out.println("Part 1: " + part1);
                long part2 = adapters.part2();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @RequiredArgsConstructor
    static class Adapters {
        final List<Integer> values;

        public int part1() {
            List<Integer> correctOrder = findChain();
            int oneJolts = countDiffs(correctOrder, 1);
            int twoJolts = countDiffs(correctOrder, 2);
            int threeJolts = countDiffs(correctOrder, 3);
            System.out.println("1J: " + oneJolts);
            System.out.println("2J: " + twoJolts);
            System.out.println("3J: " + threeJolts);
            return oneJolts * threeJolts;
        }

        private int deviceJoltage() {
            return values.stream().max(Integer::compareTo).get() + 3;
        }

        private List<Integer> findChain() {
            List<Integer> sorted = new ArrayList<>(values.stream().sorted(Integer::compareTo).collect(Collectors.toList()));
            sorted.add(deviceJoltage());
            return sorted;
        }

        private int countDiffs(List<Integer> values, int targetDiff) {
            int prev = 0;
            int count = 0;
            for (Integer value : values) {
                if (value - prev == targetDiff) count++;
                prev = value;
            }
            return count;
        }

        public long part2() {
            List<Integer> sorted = findChain();
            sorted.add(0,0);
            List<List<Integer>> groups = partition(sorted);
            return groups.stream().map(this::howManyWays).reduce(1L, (a,b) -> a * b);
        }

        private long howManyWays(List<Integer> list) {
            switch (list.size()) {
                case 1:
                case 2: return 1;
                case 3: return 2;
                case 4: return 4;
                case 5: return 7;
            }
            throw new RuntimeException("Unexpected size " + list.size());
        }

        // Also doesn't work
        public long part2Tree() {
            List<Integer> sorted = findChain();
            int target = deviceJoltage();

            // build a tree?
            Node root = new Node(null, 0);
            growTree(root, sorted, target);
            return countNodes(root, target);
        }

        List<List<Integer>> partition(List<Integer> sorted) {
            List<List<Integer>> result = new ArrayList<>();
            int prev = 0;
            List<Integer> group = new ArrayList<>();
            for (Integer i : sorted) {
                if (i == prev + 3) {
                    result.add(group);
                    group = new ArrayList<>();
                }
                group.add(i);
                prev = i;
            }
            result.add(group);

            return result;
        }

        private long countNodes(Node node, int target) {
            if (node.value < target && node.value >= target - 3) return 1;
            return node.children.stream().map(c -> countNodes(c, target)).reduce(0L, Long::sum);
        }

        private void growTree(Node node, List<Integer> values, int target) {
            Stream<Integer> canAdd = values.stream().filter(v -> v > node.value && v <= node.value + 3 && v < target);
            canAdd.forEach(v -> {
                Node newNode = new Node(node, v);
                growTree(newNode, values, target);
            });
        }

        static class Node {
            final Node parent;
            final int value;
            final Set<Node> children;

            Node(Node parent, int value) {
                children = new HashSet<>();
                this.parent = parent;
                this.value = value;
                if (parent != null) parent.children.add(this);
            }
        }


        // can't brute force this for the real input - too big
        public long part2DoesNotWork() {
            int target = deviceJoltage();
            Set<List<Integer>> allPossibleArrangements = findPossibleArrangements(Collections.singleton(Collections.emptyList()), target);
            Set<List<Integer>> allValidArrangements = filterValid(allPossibleArrangements, target);
            return allValidArrangements.size();
        }

        private Set<List<Integer>> filterValid(Set<List<Integer>> allPossibleArrangements, int target) {
            return allPossibleArrangements.stream().filter(l -> hitsTarget(l, target)).collect(Collectors.toSet());
        }

        private boolean hitsTarget(List<Integer> arrangement, int target) {
            int tail = arrangement.isEmpty() ? 0 : arrangement.get(arrangement.size() - 1);
            return (tail >= target - 3 && tail < target);
        }

        private Set<List<Integer>> findPossibleArrangements(Set<List<Integer>> fromArrangements, int target) {
            Set<List<Integer>> newArrangements = new HashSet<>(fromArrangements);
            int count = 0;
            int newCount = 0;
            do {
                count = newArrangements.size();
                newArrangements = expandArrangements(newArrangements, target);
                newCount = newArrangements.size();
            } while (count != newCount);
            return newArrangements;
        }

        private Set<List<Integer>> expandArrangements(Set<List<Integer>> fromArrangements, int target) {
            Set<List<Integer>> result = new HashSet<>(fromArrangements);
            for (List<Integer> fromArrangement : fromArrangements) {
                int tail = fromArrangement.isEmpty() ? 0 : fromArrangement.get(fromArrangement.size() - 1);
                for (Integer value : values) {
                    if (value > tail && value <= tail + 3 && value < target) {
                        List<Integer> expandedArrangement = new ArrayList<>(fromArrangement);
                        expandedArrangement.add(value);
                        result.add(expandedArrangement);
                    }
                }
            }
            return result;
        }
    }
}
