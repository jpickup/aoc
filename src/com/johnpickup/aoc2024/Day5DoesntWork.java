package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5DoesntWork {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day5-test.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());
            List<OrderRule> orderRules = new ArrayList<>();
            List<Pages> pagesList = new ArrayList<>();
            boolean hadBlank = false;
            for (String line : lines) {
                if (line.isEmpty()) {
                    hadBlank = true;
                } else {
                    if (hadBlank)
                        pagesList.add(new Pages(line));
                    else
                        orderRules.add(new OrderRule(line));
                }
            }

            int part1 = pagesList.stream().filter(pl -> pl.satisfiesAllRules(orderRules)).map(Pages::middlePage).reduce(Integer::sum).get();
            System.out.println("Part1 : " + part1);

            // There are no duplicate pages - phew!
            //System.out.println("dupes: " +pagesList.stream().filter(Pages::hasDuplicates).collect(Collectors.toList()).size());
//            List<Integer> integers = Arrays.asList(1, 2, 3, 4);
//            Set<Set<Integer>> lists = Pages.generateAllSubLists(new HashSet<>((integers)));
//            System.out.println(lists);

            List<Pages> outOfOrder = pagesList.stream().filter(pl -> !pl.satisfiesAllRules(orderRules)).collect(Collectors.toList());
            int part2 = outOfOrder.stream().map(pl -> pl.fixOrder(orderRules)).map(Pages::middlePage).reduce(Integer::sum).get();
            System.out.println("Part2 : " + part2);


        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }


    @ToString
    static class OrderRule {
        final int left;
        final int right;

        OrderRule(String line) {
            String[] parts = line.split("\\|");
            left = Integer.parseInt(parts[0]);
            right = Integer.parseInt(parts[1]);
        }
    }

    @ToString
    @RequiredArgsConstructor
    static class Pages {
        final List<Integer> pages;
        Pages(String line) {
            this(Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }

        @Override
        public String toString() {
            return pages.toString();
        }

        public boolean satisfiesAllRules(List<OrderRule> orderRules) {
            boolean result = true;
            for (OrderRule orderRule : orderRules) {
                result = result && satisfiesRule(orderRule);
            }

            return result;
        }

        private boolean satisfiesRule(OrderRule orderRule) {
            if (pages.contains(orderRule.left) && pages.contains(orderRule.right)) {
                return pages.indexOf(orderRule.left) < pages.indexOf(orderRule.right);
            } else {
                return true;
            }
        }

        public int middlePage() {
            return pages.get(pages.size() / 2);
        }

        public boolean hasDuplicates() {
            Set<Integer> distinctPages = new HashSet<>(pages);
            return distinctPages.size() != pages.size();
        }

        public Pages fixOrder(List<OrderRule> orderRules) {
            System.out.println("Fixing: " + this);

            Set<PagePair> problemPagePairs = breaksAnyRules(orderRules);
            Set<Integer> problemPages = new HashSet<>();
            problemPages.addAll(problemPagePairs.stream().map(pair -> pair.left).collect(Collectors.toSet()));
            problemPages.addAll(problemPagePairs.stream().map(pair -> pair.right).collect(Collectors.toSet()));
            System.out.printf("Problem pairs (%d): %s%n", problemPagePairs.size(), problemPagePairs);
            System.out.printf("Problem pages (%d): %s%n", problemPages.size(), problemPages);

            Set<PagePair> possiblePagePairs = generatePossiblePairs(problemPages);
            System.out.printf("Possible page pairs (%d): %s%n", problemPagePairs.size(), problemPagePairs);

            Set<Set<PagePair>> allSubLists = generateAllSubLists(problemPagePairs);
            System.out.printf("All sub-lists (%d): %s%n", allSubLists.size(), allSubLists);

            for (Set<PagePair> subList : allSubLists) {
                Pages flippedPages = new Pages(this.pages);
                for (PagePair pagePair : subList) {
                    flippedPages = flippedPages.flip(pagePair);
                    System.out.println("Trying: " + flippedPages);

                    if (flippedPages.satisfiesAllRules(orderRules)) {
                        System.out.println("Found: " + flippedPages);
                        return flippedPages;
                    }
                }
            }
            throw new RuntimeException("Failed");
        }

        private Set<PagePair> generatePossiblePairs(Set<Integer> problemPages) {
            Set<PagePair> result = new HashSet<>();
            for (Integer problemPage1 : problemPages) {
                for (Integer problemPage2 : problemPages) {
                    if (!problemPage1.equals(problemPage2)) {
                        result.add(new PagePair(problemPage1, problemPage2));
                    }
                }
            }
            return result;
        }

        static <T> Set<Set<T>> generateAllSubLists(Set<T> original) {
            if (original.size() <= 1) return Collections.singleton(original);

            Set<Set<T>> result = new HashSet<>();

            for (T item : original) {
                Set<T> withoutOne = new HashSet<>(original);
                withoutOne.remove(item);
                Set<Set<T>> subLists = generateAllSubLists(withoutOne);
                result.addAll(subLists);
                for (Set<T> subList : subLists) {
                    for (int j = 0; j < subList.size(); j++) {
                        Set<T> subListWithItem = new HashSet<>(subList);
                        subListWithItem.add(item);
                        result.add(subListWithItem);
                    }
                }
            }
            return result;
        }

        private Pages flip(PagePair pagePair) {
            List<Integer> newPages = new ArrayList<>(pages);
            int leftIndex = pages.indexOf(pagePair.left);
            int rightIndex = pages.indexOf(pagePair.right);
            if (leftIndex < rightIndex) {
                newPages.remove(rightIndex);
                newPages.remove(leftIndex);
                newPages.add(leftIndex, pagePair.right);
                newPages.add(rightIndex, pagePair.left);
            } else {
                newPages.remove(leftIndex);
                newPages.remove(rightIndex);
                newPages.add(rightIndex, pagePair.left);
                newPages.add(leftIndex, pagePair.right);
            }
            return new Pages(newPages);
        }

        private Set<PagePair> breaksAnyRules(List<OrderRule> orderRules) {
            Set<PagePair> result = new HashSet<>();
            for (OrderRule orderRule : orderRules) {
                result.addAll(breaksRule(orderRule));
            }
            return result;
        }

        private List<PagePair> breaksRule(OrderRule orderRule) {
            if (pages.contains(orderRule.left) && pages.contains(orderRule.right) && pages.indexOf(orderRule.left) > pages.indexOf(orderRule.right)) {
                return Collections.singletonList(new PagePair(orderRule.left, orderRule.right));
            } else {
                return Collections.emptyList();
            }
        }
    }


    @Data
    @RequiredArgsConstructor
    static class PagePair {
        final int left;
        final int right;

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof PagePair)) return false;
            PagePair otherPagePair = (PagePair) other;
            return ((this.left == otherPagePair.left)
                    && (this.right == otherPagePair.right))
                    || ((this.left == otherPagePair.right)
                    && (this.right == otherPagePair.left));
        }

        @Override
        public int hashCode() {
            return left + right;
        }

        public int smallest() {
            return left < right ? left : right;
        }
        public int largest() {
            return left > right ? left : right;
        }

        public PagePair flip() {
            return new PagePair(right, left);
        }
    }
}
