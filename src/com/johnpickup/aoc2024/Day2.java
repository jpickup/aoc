package com.johnpickup.aoc2024;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day2.txt"))) {
            List<Levels> levelsList = stream.filter(s -> !s.isEmpty()).map(Levels::new).collect(Collectors.toList());
            System.out.println("Part 1 : " + levelsList.stream().filter(Levels::isSafe).count());
            System.out.println("Part 2 : " + levelsList.stream().filter(Levels::isMainlySafe).count());
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    static class Levels {
        final List<Integer> levels;

        Levels(String input) {
            this(Arrays.stream(input.split(" ")).map(Integer::parseInt).collect(Collectors.toList()));
        }

        boolean isSafe() {
            boolean result = true;
            boolean increasing = levels.get(0) < levels.get(1);
            Integer prevLevel = null;

            for (Integer level : levels) {
                if (prevLevel != null) {
                    int diff = level - prevLevel;
                    if (increasing) {
                        result = result && (diff >=1) && (diff <= 3);
                    } else {
                        result = result && (diff <=-1) && (diff >= -3);
                    }
                }
                prevLevel = level;
            }
            return result;
        }

        // levels are mainly-safe if they are safe or are safe with one level ignored
        boolean isMainlySafe() {
            if (isSafe()) return true;

            for (int i = 0; i < levels.size(); i++) {
                Levels newLevels = new Levels(copyListWithoutItem(this.levels, i));
                if (newLevels.isSafe()) return true;
            }
            return false;
        }

        private List<Integer> copyListWithoutItem(List<Integer> list, int skipIndex) {
            List<Integer> result = new ArrayList<>(list);
            result.remove(skipIndex);
            return result;
        }
    }
}
