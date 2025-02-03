package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day15.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());
            List<String> words = Arrays.stream(lines.get(0).split(",")).collect(Collectors.toList());

            Long part1 = words.stream().map(Day15::hash).reduce(0L, Long::sum);
            System.out.println("Part 1 : " + part1);

            List<Step> steps = words.stream().map(Step::parse).collect(Collectors.toList());
            Map<Long, List<Lens>> boxes = new HashMap<>();
            for (Step step : steps) {
                if (!boxes.containsKey(step.hash)) boxes.put(step.hash, new ArrayList<>());
                List<Lens> boxContents = boxes.get(step.hash);
                if (step.op == '-') {
                    for (int i = 0; i < boxContents.size(); i++) {
                        if (boxContents.get(i).label.equals(step.label)) {
                            boxContents.remove(i);
                            break;
                        }
                    }
                }
                if (step.op == '=') {
                    boolean found = false;
                    for (int i = 0; i < boxContents.size(); i++) {
                        if (boxContents.get(i).label.equals(step.label)) {
                            boxContents.remove(i);
                            boxContents.add(i, new Lens(step.label, step.focalLength));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        boxContents.add(new Lens(step.label, step.focalLength));
                    }
                }
            }

            long part2 = 0L;
            for (Map.Entry<Long, List<Lens>> boxEntry : boxes.entrySet()) {
                int lensIdx = 1;
                for (Lens lens : boxEntry.getValue()) {
                    long power = (boxEntry.getKey()+1)
                            * lensIdx
                            * lens.focalLength;
                    part2 += power;
                    lensIdx++;
                }
            }
            System.out.println("Part 2 : " + part2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    private static long hash(String s) {
        long result = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            result = ((result + ch) * 17L) % 256;
        }
        return result;
    }

    @RequiredArgsConstructor
    @Data
    static class Step {
        final String label;
        final char op;
        final int focalLength;
        final long hash;

        public static Step parse(String s) {
            boolean eq = s.contains("=");

            String[] parts = s.split(eq ? "=" : "-");
            return new Step(parts[0], eq?'=':'-', eq?Integer.parseInt(parts[1]):0,Day15.hash(parts[0]));
        }
    }

    @RequiredArgsConstructor
    static class Lens {
        final String label;
        final int focalLength;
    }
}
