package com.johnpickup.aoc2020;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2020/Day7/Day7.txt"))) {
            List<BagRule> bagRules = stream.filter(s -> !s.isEmpty()).map(BagRule::new).collect(Collectors.toList());

            System.out.println(bagRules);

            long part1 = BagRule.part1();
            System.out.println("Part 1: " + part1);
            long part2 = BagRule.part2();
            System.out.println("Part 2: " + part2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    static final Pattern parserPattern = Pattern.compile("([a-z\\W].*) bags contain (((([0-9]+)([a-z ].*))|no other) bags?,?)*\\.");

    @ToString
    static class BagRule {
        final String name;
        final Map<String, Integer> contains = new HashMap<>();
        static Map<String, BagRule> bagRules = new HashMap<>();

        static String target = "shiny gold";
        static int part1() {
            int result = 0;

            for (BagRule bagRule : bagRules.values()) {
                if (bagRule.canContain(target)) result++;
            }
            return result;
        }

        static int part2() {
            BagRule targetBag = bagRules.get(target);
            return targetBag.bagCount()-1;
        }

        private int bagCount() {
            int result = 1;
            for (Map.Entry<String, Integer> bagEntry : contains.entrySet()) {
                result += bagRules.get(bagEntry.getKey()).bagCount() * bagEntry.getValue() ;
            }

            return result;
        }

        private boolean canContain(String target) {
            if (contains.containsKey(target)) return true;
            for (String containedBagName : contains.keySet()) {
                BagRule bagRule = bagRules.get(containedBagName);
                if (bagRule.canContain(target)) return true;
            }
            return false;
        }

        BagRule(String line) {
            Matcher matcher = parserPattern.matcher(line);
            if (!matcher.matches()) throw new RuntimeException("Failed to match: " + line);
            name = matcher.group(1);
            String innerBags = matcher.group(2);
            String[] bags = innerBags.split(", ");

            for (String bag : bags) {
                if (!bag.equals("no other bags")) {
                    int count = Integer.parseInt(bag.substring(0, 1));
                    String bagName = bag.substring(2).replace("bags", "").replace("bag", "").trim();
                    contains.put(bagName, count);
                }
            }
            bagRules.put(this.name, this);
//            System.out.println(line);
//            System.out.println(Arrays.stream(bags).collect(Collectors.joining(" & ")));
//            for (int i = 1; i < matcher.groupCount(); i++) {
//                System.out.printf("%d : %s %n", i, matcher.group(i));
//            }
        }
    }
}
