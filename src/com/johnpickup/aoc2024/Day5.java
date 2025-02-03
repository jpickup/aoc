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

public class Day5 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2024/Day5/Day5.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());
            List<OrderRule> orderRules = new ArrayList<>();
            List<PageList> pageLists = new ArrayList<>();
            boolean hadBlank = false;
            for (String line : lines) {
                if (line.isEmpty()) {
                    hadBlank = true;
                } else {
                    if (hadBlank)
                        pageLists.add(new PageList(line));
                    else
                        orderRules.add(new OrderRule(line));
                }
            }

            int part1 = pageLists.stream().filter(pl -> pl.satisfiesAllRules(orderRules)).map(PageList::middlePage).reduce(Integer::sum).get();
            System.out.println("Part1 : " + part1);

            List<PageList> outOfOrder = pageLists.stream().filter(pl -> !pl.satisfiesAllRules(orderRules)).collect(Collectors.toList());
            int part2 = outOfOrder.stream().map(pl -> pl.fixOrder(orderRules)).map(PageList::middlePage).reduce(Integer::sum).get();
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

    @RequiredArgsConstructor
    static class PageList {
        final List<Integer> pages;
        PageList() {
            this(Collections.emptyList());
        }
        PageList(String line) {
            this(Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }

        @Override
        public String toString() {
            return pages.toString();
        }

        public boolean satisfiesAllRules(List<OrderRule> orderRules) {
            return orderRules.stream().allMatch(this::satisfiesRule);
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

        public PageList fixOrder(List<OrderRule> orderRules) {
            // build up a valid page list by adding each page from the invalid one in a location
            // that matches all the rules. during construction there may be many orders of the incomplete set
            // that work, so we need to work on all of them
            Set<PageList> result = Collections.singleton(new PageList());
            for (Integer pageNumber : pages) {
                result = findPossibleOrders(result, orderRules, pageNumber);
            }
            if (result.size() == 1) {
                return result.stream().findFirst().get();
            }

            throw new RuntimeException("Failed " + result);
        }

        // find all possible page lists that, after adding the requested page, still match the rules
        private static Set<PageList> findPossibleOrders(Set<PageList> pageListSet, List<OrderRule> orderRules, int pageToAdd) {
            Set<PageList> result = new HashSet<>();
            for (PageList pageList : pageListSet) {
                if (pageList.pages.size() == 0) {
                    result.add(new PageList(Collections.singletonList(pageToAdd)));
                } else {
                    for (int i = 0; i <= pageList.pages.size(); i++) {
                        List<Integer> newPagesList = new ArrayList<>(pageList.pages);
                        newPagesList.add(i, pageToAdd);
                        PageList newPageList = new PageList(newPagesList);
                        if (newPageList.satisfiesAllRules(orderRules)) {
                            result.add(newPageList);
                        }
                    }
                }
            }
            return result;
        }
    }

    @Data
    @RequiredArgsConstructor
    static class PagePair {
        final int left;
        final int right;
    }
}
