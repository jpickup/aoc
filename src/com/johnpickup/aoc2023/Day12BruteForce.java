package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Only good for part 1
public class Day12BruteForce {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day12.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            List<String> substitutedLines = new ArrayList<>();

            for (String line : lines) {
                substitutedLines.addAll(substitute(line));
            }

            List<Springs> springLines = substitutedLines.stream().filter(s -> !s.isEmpty()).map(Springs::parse).collect(Collectors.toList());
            //System.out.println(springLines);

            long part1 = springLines.stream().map(Springs::combinations).reduce(0L, Long::sum);
            System.out.println("Part 1: " + part1); // 7541

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static List<String> substitute(String line) {
        List<String> result = new ArrayList<>();

        if (line.isEmpty()) return Collections.singletonList("");
        if (line.charAt(0) == '?') {
            result.addAll(substitute(line.substring(1)).stream().map(s -> "." + s).collect(Collectors.toList()));
            result.addAll(substitute(line.substring(1)).stream().map(s -> "#" + s).collect(Collectors.toList()));

        } else {
            result.addAll(substitute(line.substring(1)).stream().map(s -> line.charAt(0) + s).collect(Collectors.toList()));
        }
        return result;
    }

    @RequiredArgsConstructor
    @Data
    static class Springs {
        final List<State> states;
        final List<Integer> brokenGroups;
        public static Springs parse(String s) {
            String[] parts = s.split(" ");
            List<State> states = new ArrayList<>();
            for (int i=0; i < parts[0].length(); i++) states.add(State.parse(parts[0].charAt(i)));
            List<Integer> numbers = Arrays.stream(parts[1].split(",")).map(Integer::parseInt).collect(Collectors.toList());
            return new Springs(states, numbers);
        }

        public long combinations() {
            if (states.isEmpty()) return brokenGroups.isEmpty()?1:0;

            //System.out.println(this);

            long result;
            List<State> nextStates = states.subList(1, states.size());

            switch (states.get(0)) {
                case OK:
                    result = new Springs(nextStates, brokenGroups).combinations();
                    break;
                case BROKEN:
                    if (brokenGroups.isEmpty()) return 0;
                    List<Integer> newBrokenGroups = new ArrayList<>();
                    if (brokenGroups.get(0) == 1) {
                        // end of a group so next state must not be broken
                        if (!nextStates.isEmpty() && nextStates.get(0) == State.BROKEN) return 0;
                    }
                    else {
                        // within a group so next state must not be OK
                        if (!nextStates.isEmpty() && nextStates.get(0) == State.OK) return 0;
                        newBrokenGroups.add(brokenGroups.get(0) - 1);
                    }
                    newBrokenGroups.addAll(brokenGroups.subList(1, brokenGroups.size()));
                    result = new Springs(nextStates, newBrokenGroups).combinations();
                    break;
                default:
                    throw new RuntimeException("Unknown state");
            }
            //System.out.println(this + " = " + result);
            return result;
        }

    }

    @RequiredArgsConstructor
    enum State {
        UNKNOWN('?'),
        BROKEN('#'),
        OK('.');
        final char ch;
        static State parse(char ch) {
            switch (ch) {
                case '?' : return UNKNOWN;
                case '#' : return BROKEN;
                case '.' : return OK;
                default: throw new RuntimeException("Invalid input " + ch);
            }
        }
    }
}
