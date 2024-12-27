package com.johnpickup.aoc2020;

import com.johnpickup.util.Range;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day16 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2020/" + day + "/" + day;
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
                List<String> lines = stream
                        .collect(Collectors.toList());

                List<String> group = new ArrayList<>();
                int groupIdx = 0;
                for (String line : lines) {
                    if (line.isEmpty()) {
                        processGroup(groupIdx++, group);
                        group.clear();
                    } else {
                        group.add(line);
                    }
                }

                System.out.println(ticketRules);
                System.out.println(myTicket);
                System.out.println(nearbyTickets);

                List<Integer> invalidValues = nearbyTickets.stream().map(t -> t.invalidFields(ticketRules)).flatMap(List::stream).collect(Collectors.toList());
                long part1 = invalidValues.stream().reduce(0, Integer::sum);
                System.out.println("Part 1: " + part1);

                List<String> ruleNames = ticketRules.names();
                Map<String, Set<Integer>> possibleRuleIndexes = new HashMap<>();
                Set<Integer> possibleIndexes = IntStream.rangeClosed(0, myTicket.values.size()-1).boxed().collect(Collectors.toSet());
                ruleNames.forEach(r -> possibleRuleIndexes.put(r, new HashSet<>(possibleIndexes)));

                List<Ticket> validTickets = nearbyTickets.stream().filter(t -> t.isValid(ticketRules)).collect(Collectors.toList());

                Map<String, Integer> singles = Collections.emptyMap();
                while (!complete(possibleRuleIndexes)) {
                    for (Map.Entry<String, Set<Integer>> entry : possibleRuleIndexes.entrySet()) {
                        for (Ticket validTicket : validTickets) {
                            Set<Integer> invalidIndexes = validTicket.impossibleIndexes(ticketRules.getByName(entry.getKey()), entry.getValue());
                            entry.getValue().removeAll(invalidIndexes);
                        }
                    }
                    singles = possibleRuleIndexes.entrySet().stream()
                            .filter(e -> e.getValue().size() == 1)
                            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().findFirst()
                                    .orElseThrow(() -> new RuntimeException("No item in single item set!"))));

                    for (Map.Entry<String, Set<Integer>> entry : possibleRuleIndexes.entrySet()) {
                        for (Map.Entry<String, Integer> singleEntry : singles.entrySet()) {
                            if (!singleEntry.getKey().equals(entry.getKey())) {
                                entry.getValue().remove(singleEntry.getValue());
                            }
                        }
                    }
                }
                long part2 = ruleNames.stream().filter(n -> n.startsWith("departure"))
                        .map(singles::get)
                        .map(idx -> myTicket.values.get(idx))
                        .map(v -> (long)v)
                        .reduce(1L, (a, b) -> (a * b));
                System.out.println("Part 2: " + part2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static boolean complete(Map<String, Set<Integer>> possibleRuleIndexes) {
        // we are done if all possible sets have just a single item
        return possibleRuleIndexes.values().stream().map(Set::size).allMatch(s -> s == 1);
    }

    static TicketRules ticketRules;
    static Ticket myTicket;
    static List<Ticket> nearbyTickets;

    private static void processGroup(int groupIdx, List<String> group) {
        switch (groupIdx) {
            case 0:
                ticketRules = new TicketRules(group);
                break;
            case 1:
                myTicket = new Ticket(group.get(1));
                break;
            case 2:
                nearbyTickets = group.subList(1, group.size()).stream().map(Ticket::new).collect(Collectors.toList());
                break;
        }
    }

    @ToString
    static class Ticket {
        final List<Integer> values;
        Ticket(String line) {
            values = Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        }

        public List<Integer> invalidFields(TicketRules ticketRules) {
            return values.stream().filter(ticketRules::matchesNone).collect(Collectors.toList());
        }

        public boolean isValid(TicketRules ticketRules) {
            return invalidFields(ticketRules).isEmpty();
        }

        public Set<Integer> impossibleIndexes(TicketRule rule, Set<Integer> availableIndexes) {
            Set<Integer> result = new HashSet<>();
            for (Integer availableIndex : availableIndexes) {
                int valueForIndex = values.get(availableIndex);
                boolean validForIndex = rule.isValid(valueForIndex);
                if (!validForIndex) result.add(availableIndex);
            }
            return result;
        }
    }

    @ToString
    static class TicketRules {
        final List<TicketRule> rules;
        TicketRules(List<String> lines) {
            rules = lines.stream().map(TicketRule::new).collect(Collectors.toList());
        }

        public boolean matchesNone(Integer value) {
            return rules.stream().noneMatch(r -> r.isValid(value));
        }

        public List<String> names() {
            return rules.stream().map(r -> r.name).collect(Collectors.toList());
        }

        public TicketRule getByName(String ruleName) {
            return rules.stream().filter(r -> r.name.equals(ruleName)).findFirst().orElseThrow(() -> new RuntimeException("No rule found with name " + ruleName));
        }
    }

    @ToString
    static class TicketRule {
        final String name;
        final List<Range<Integer>> validRanges;
        TicketRule(String line) {
            String[] parts = line.split(": ");
            name = parts[0];
            validRanges = Arrays.stream(parts[1].split(" or ")).map(s -> new Range<Integer>(s, Integer::parseInt)).collect(Collectors.toList());
        }

        public boolean isValid(Integer value) {
            return validRanges.stream().anyMatch(r -> r.containsValue(value));
        }
    }
}
