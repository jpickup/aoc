package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12Part2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day12.txt"))) {
            List<Springs> springLines = stream.filter(s -> !s.isEmpty()).map(Springs::parse).collect(Collectors.toList());
            //System.out.println(springLines);
            springLines = duplicateSprings(springLines);

            long part2 = springLines.stream().map(Springs::combinations).reduce(0L, Long::sum);
            System.out.println("Part 2: " + part2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static List<Springs> duplicateSprings(List<Springs> springLines) {
        return springLines.stream().map(s -> s.duplicate(5)).collect(Collectors.toList());
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

        static int progress = 0;
        static long startTime = System.currentTimeMillis();

        public long combinations() {
            long result = combinations(false);
            System.out.printf("Line #%d = %d in %dms%n", ++progress, result, System.currentTimeMillis()-startTime);
            return result;
        }

        public long combinations(boolean inGroup) {
            if (states.isEmpty()) return brokenGroups.isEmpty()?1:0;

            //System.out.println(this + " inGroup = " + inGroup);

            long result;
            List<State> nextStates = states.subList(1, states.size());

            switch (states.get(0)) {
                case OK:
                    if (inGroup) return 0;
                    result = new Springs(nextStates, brokenGroups).combinations(false);
                    break;
                case BROKEN:
                    if (brokenGroups.isEmpty()) return 0;
                    List<Integer> newBrokenGroups = new ArrayList<>();
                    if (brokenGroups.get(0) == 1) {
                        // end of a group so next state must not be broken
                        if (!nextStates.isEmpty() && nextStates.get(0) == State.BROKEN) return 0;
                        // next UNKNOWN has to be OK - can't choose BROKEN
                        if (!nextStates.isEmpty() && nextStates.get(0) == State.UNKNOWN) {
                            newBrokenGroups.addAll(brokenGroups.subList(1, brokenGroups.size()));
                            List<State> statesWithOK = new ArrayList<>();
                            statesWithOK.add(State.OK);
                            statesWithOK.addAll(nextStates.subList(1, nextStates.size()));
                            return new Springs(statesWithOK, newBrokenGroups).combinations(false);
                        }
                    }
                    else {
                        // within a group so next state must not be OK
                        if (!nextStates.isEmpty() && nextStates.get(0) == State.OK) return 0;

                        // if the next state is UNKNOWN then it has to be chosen as BROKEN
                        if (!nextStates.isEmpty() && nextStates.get(0) == State.UNKNOWN) {
                            List<State> statesWithBroken = new ArrayList<>();
                            statesWithBroken.add(State.BROKEN);
                            statesWithBroken.addAll(nextStates.subList(1,nextStates.size()));
                            newBrokenGroups.add(brokenGroups.get(0) - 1);
                            newBrokenGroups.addAll(brokenGroups.subList(1, brokenGroups.size()));
                        return new Springs(statesWithBroken, newBrokenGroups).combinations(true);
                        }
                        newBrokenGroups.add(brokenGroups.get(0) - 1);
                    }
                    newBrokenGroups.addAll(brokenGroups.subList(1, brokenGroups.size()));
                    result = new Springs(nextStates, newBrokenGroups).combinations(brokenGroups.get(0) > 1);
                    break;
                case UNKNOWN:
                    // two possibilities either it's a broken or OK - try them both
                    List<State> statesWithBroken = new ArrayList<>();
                    statesWithBroken.add(State.BROKEN);
                    statesWithBroken.addAll(nextStates);
                    List<State> statesWithOK = new ArrayList<>();
                    statesWithOK.add(State.OK);
                    statesWithOK.addAll(nextStates);
                    result = new Springs(statesWithOK, brokenGroups).combinations(false)
                            + new Springs(statesWithBroken, brokenGroups).combinations(true);
                    break;
                default:
                    throw new RuntimeException("Unknown state");
            }
            //System.out.println(this + " = " + result);
            return result;
        }

        public Springs duplicate(int number) {
            List<State> duplicatedStates = new ArrayList<>(states);
            List<Integer> duplicatedGroups = new ArrayList<>(brokenGroups);

            for (int i = 1; i < number; i ++) {
                duplicatedStates.add(State.UNKNOWN);
                duplicatedStates.addAll(states);
                duplicatedGroups.addAll(brokenGroups);
            }

            return new Springs(duplicatedStates, duplicatedGroups);
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
