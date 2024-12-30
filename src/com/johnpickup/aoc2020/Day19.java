package com.johnpickup.aoc2020;

import com.johnpickup.util.InputUtils;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day19 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .collect(Collectors.toList());

                List<List<String>> groups = InputUtils.splitIntoGroups(lines);

                Rules rules = new Rules(groups.get(0));
                List<String> inputs = groups.get(1);

                long part1 = rules.countValid(inputs);
                System.out.println("Part 1: " + part1);

                rules.updateRule("8: 42 | 42 8");
                rules.updateRule("11: 42 31 | 42 11 31");
                long part2 = rules.countValid(inputs);
                System.out.println("Part 2: " + part2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Rules {
        static Rules instance = null;
        final Map<Integer, Rule> rules;
        Rules(List<String> lines) {
            instance = this;
            rules = lines.stream().map(Rule::parse).collect(Collectors.toMap(r -> r.number, r -> r));
        }

        public static Rules getInstance() {
            return instance;
        }

        public Rule getRule(int n) {
            return rules.get(n);
        }

        public long countValid(List<String> inputs) {
            Rule rule0 = rules.get(0);
            return inputs.stream().filter(rule0::matches).count();
        }

        public boolean matches(String input) {
            return rules.get(0).matches(input);
        }

        public void updateRule(String newRule) {
            Rule r = Rule.parse(newRule);
            rules.put(r.number, r);
        }
    }

    @RequiredArgsConstructor
    static abstract class Rule {
        final int number;
        static Rule parse(String line) {
            String[] parts = line.split(":");
            int number = Integer.parseInt(parts[0]);
            if (parts[1].contains("\"")) {
                return new LiteralRule(number, parts[1].trim());
            } else {
                return new CompoundRule(number, parts[1].trim());
            }
        }

        boolean matches(String input) {
            List<String> possibleRemainders = consume(Collections.singletonList(input));
            return possibleRemainders.stream().anyMatch(String::isEmpty);
        }
        abstract List<String> consume(List<String> inputs);
    }

    static class LiteralRule extends Rule {
        final char ch;
        LiteralRule(int number, String s) {
            super(number);
            ch = s.replaceAll("\"", "").charAt(0);
        }

        @Override
        List<String> consume(List<String> inputs) {
            List<String> result = new ArrayList<>();
            for (String input : inputs) {
                if (!input.isEmpty() && input.charAt(0) == ch)
                    result.add(input.substring(1));
            }
            return result;
        }
    }

    static class CompoundRule extends Rule {
        final List<List<Integer>> ruleSets;
        CompoundRule(int number, String s) {
            super(number);
            String[] parts = s.split("\\|");
            ruleSets = Arrays.stream(parts).map(p -> toIntList(p.trim())).collect(Collectors.toList());
        }

        private List<Integer> toIntList(String s) {
            String[] parts = s.split(" ");
            return Arrays.stream(parts).map(Integer::parseInt).collect(Collectors.toList());
        }

        @Override
        List<String> consume(List<String> inputs) {
            List<String> result = new ArrayList<>();
            for (List<Integer> ruleSet : ruleSets) {
                List<String> ruleSetResult = ruleSetConsume(ruleSet, inputs);
                result.addAll(ruleSetResult);
            }
            return result;
        }

        private List<String> ruleSetConsume(List<Integer> ruleNumbers, List<String> inputs) {
            List<Rule> rules = ruleNumbers.stream().map(n -> Rules.getInstance().getRule(n)).collect(Collectors.toList());
            List<String> result = new ArrayList<>();
            for (String input : inputs) {
                List<String> remaining = Collections.singletonList(input);
                // try each input across all the rules in the rule set
                for (Rule rule : rules) {
                    remaining = rule.consume(remaining);
                }
                result.addAll(remaining);
            }
            return result;
        }
    }
}
