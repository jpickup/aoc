package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day4.txt"))) {
            List<Card> cards = stream.filter(s -> !s.isEmpty()).map(Card::parse).collect(Collectors.toList());

            Long part1 = cards.stream().map(Card::score).reduce(0L, Long::sum);
            System.out.println("Part 1 : " + part1);

            long[] counts = new long[cards.size()+1];
            for (int cardIdx = 1; cardIdx <= cards.size(); cardIdx++) {
                counts[cardIdx]=1;
            }

            for (int cardIdx = 0; cardIdx < cards.size(); cardIdx++) {
                long matches = cards.get(cardIdx).matches();
                for (int matchIdx = 1; matchIdx <= matches; matchIdx++) {
                    if (cardIdx+matchIdx+1 < counts.length) {
                        counts[cardIdx+matchIdx+1] += counts[cardIdx+1];
                    }
                }
            }
            long part2 = Arrays.stream(counts).reduce(0L, Long::sum);
            System.out.println("Part 2 : " + part2);  // 7258152

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "(ms)");
    }

    @RequiredArgsConstructor
    @Data
    static class Card {
        final long number;
        final Set<Long> winning;
        final Set<Long> actual;
        static Card parse(String s) {
            String[] crd = s.split(":");
            long crdNo = Long.parseLong(crd[0].replace("Card","").trim());
            String[] nums = crd[1].split("\\|");
            Set<Long> wins = Arrays.stream(nums[0].split(" ")).map(String::trim).filter(n -> !n.isEmpty()).map(Long::parseLong).collect(Collectors.toSet());
            Set<Long> vals = Arrays.stream(nums[1].split(" ")).map(String::trim).filter(n -> !n.isEmpty()).map(Long::parseLong).collect(Collectors.toSet());
            return new Card(crdNo, wins, vals);
        }
        long score() {
            long result = 0L;
            for (Long num : actual) {
                if (winning.contains(num)) {
                    if (result == 0L) result = 1L; else result *= 2;
                }
            }
            return result;
        }

        long matches() {
            long result = 0L;
            for (Long num : actual) {
                if (winning.contains(num)) {
                    result++;
                }
            }
            return result;
        }

    }

}
