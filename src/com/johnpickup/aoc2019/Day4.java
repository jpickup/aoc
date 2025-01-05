package com.johnpickup.aoc2019;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day4 {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        int lower = 272091;
        int upper = 815432;

        List<Integer> possible = new ArrayList<>();

        for (int i = lower; i <= upper; i++) {
            if (isPossible(i)) possible.add(i);
        }

        long part1 = possible.size();
        System.out.println("Part 1: " + part1);

        long part2 = possible.stream().filter(Day4::satisfiesPart2).count();
        System.out.println("Part 2: " + part2);

        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static boolean satisfiesPart2(Integer value) {
        Set<String> groups = groups(Integer.toString(value));
        return groups.stream().anyMatch(s -> s.length() == 2);
    }

    private static Set<String> groups(String s) {
        Set<String> result = new HashSet<>();
        String part = "";
        char prev = 0;
        for (int i = 0 ; i < s.length(); i++) {
            char curr = s.charAt(i);
            if (curr != prev) {
                if (!part.isEmpty()) result.add(part);
                part = "";
            }
            part = part + curr;
            prev = curr;
        }
        if (!part.isEmpty()) result.add(part);
        return result;
    }

    private static boolean isPossible(int value) {
        boolean hasPair = false;
        boolean isIncreasing = true;
        String s = Integer.toString(value);
        for (int i = 1 ; i < s.length(); i++) {
            char prev = s.charAt(i-1);
            char curr = s.charAt(i);
            if (prev==curr) hasPair = true;
            isIncreasing &= (curr >= prev);
        }
        return hasPair && isIncreasing;
    }
}

