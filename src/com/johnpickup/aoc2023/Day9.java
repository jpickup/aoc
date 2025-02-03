package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2023/Day9.txt"))) {
            List<Sequence> sequences = stream.filter(s -> !s.isEmpty()).map(Sequence::parse).collect(Collectors.toList());
            Integer part1 = sequences.stream().map(Sequence::part1).map(Sequence::getLast).reduce(0, Integer::sum);
            System.out.println(part1);

            Integer part2 = sequences.stream().map(Sequence::part2).map(Sequence::getFirst).reduce(0, Integer::sum);
            System.out.println(part2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @RequiredArgsConstructor
    @Data
    static class Sequence {
        public static Sequence parse(String s) {
            return new Sequence(Arrays.stream(s.split(" ")).map(Integer::valueOf).collect(Collectors.toList()));
        }

        public Sequence part1() {
            List<List<Integer>> sequences = new ArrayList<>();
            sequences.add(numbers);
            List<Integer> diffs = numbers;
            do {
                diffs = generateDiffs(diffs);
                sequences.add(diffs);
            } while (!allZero(diffs));

            List<Integer> prev = null;
            for (int i = sequences.size() - 1 ; i >=0; i--) {
                List<Integer> thisSeq = sequences.get(i);
                int nextVal = (prev == null ? 0 : prev.get(prev.size() - 1)) + thisSeq.get(thisSeq.size()-1);
                thisSeq.add(nextVal);
                prev = thisSeq;
            }
            return new Sequence(sequences.get(0));
        }

        public Sequence part2() {
            List<List<Integer>> sequences = new ArrayList<>();
            sequences.add(numbers);
            List<Integer> diffs = numbers;
            do {
                diffs = generateDiffs(diffs);
                sequences.add(diffs);
            } while (!allZero(diffs));

            List<Integer> prev = null;
            for (int i = sequences.size() - 1 ; i >=0; i--) {
                List<Integer> thisSeq = sequences.get(i);
                int nextVal = thisSeq.get(0) - (prev == null ? 0 : prev.get(0));
                thisSeq.add(0, nextVal);
                prev = thisSeq;
            }
            return new Sequence(sequences.get(0));
        }

        private static List<Integer> generateDiffs(List<Integer> is) {
            List<Integer> result = new ArrayList<>();
            for (int idx = 1; idx < is.size(); idx++) {
                result.add(is.get(idx) - is.get(idx-1));
            }
            return result;
        }

        private static boolean allZero(List<Integer> is) {
            return is.stream().allMatch(i -> i == 0);
        }

        final List<Integer> numbers;

        public int getLast() {
            return numbers.get(numbers.size()-1);
        }
        public int getFirst() {
            return numbers.get(0);
        }
    }



}
