package com.johnpickup.aoc2023;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2023/Day7.txt"))) {
            List<String> lines = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList());

            Map<Hand, Integer> hands = new TreeMap<>();
            for (String line : lines) {
                String[] parts = line.split(" ");
                hands.put(Hand.parse(parts[0]), Integer.valueOf(parts[1]));
            }

            long result = 0;
            int i = 1;
            for (Map.Entry<Hand, Integer> handEntry : hands.entrySet()) {
                result += (long) handEntry.getValue() * i;
                i++;
            }
            System.out.println(result);
            // Part 1 = 246163188
            // Part 2 = 245794069
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @Data
    static class Hand implements Comparable<Hand> {
        final Character[] cards = new Character[5];
        public static Hand parse(String s) {
            if (s.length() != 5) throw new RuntimeException("Invalid hand input: " + s);
            Hand result = new Hand();
            for (int i = 0; i < 5; i++) {
                result.cards[i] = s.charAt(i);
            }
            return result;
        }

        public Rank rank() {
            Map<Character, Long> counts = Arrays.stream(cards)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            Long jokers = counts.getOrDefault('J', 0L);
            Map<Character, Long> countsNoJokers = counts.entrySet().stream()
                    .filter(e -> e.getKey() != 'J')
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (counts.containsValue(5L)
                    || (jokers == 4 && countsNoJokers.containsValue(1L))
                    || (jokers == 3 && countsNoJokers.containsValue(2L))
                    || (jokers == 2 && countsNoJokers.containsValue(3L))
                    || (jokers == 1 && countsNoJokers.containsValue(4L))
            ) return Rank.FIVE_OF_A_KIND;

            if (counts.containsValue(4L)
                    || (jokers == 3 && countsNoJokers.containsValue(1L))
                    || (jokers == 2 && countsNoJokers.containsValue(2L))
                    || (jokers == 1 && countsNoJokers.containsValue(3L))
            ) return Rank.FOUR_OF_A_KIND;

            if ((counts.containsValue(3L) && countsNoJokers.containsValue(2L))
                    || (jokers == 1 && countsNoJokers.containsValue(2L) && counts.size()==3)
                    || (jokers == 2 && countsNoJokers.containsValue(2L) && counts.size()==3)
            ) return Rank.FULL_HOUSE;

            if (counts.containsValue(3L)
                    || (jokers == 1 && countsNoJokers.containsValue(2L))
                    || (jokers == 2)
            ) return Rank.THREE_OF_A_KIND;

            if (counts.containsValue(2L) && counts.size()==3) return Rank.TWO_PAIRS;

            if (counts.containsValue(2L)
                    || (jokers == 1)) return Rank.PAIR;

            return Rank.ONE_OF_A_KIND;
        }

        @Override
        public int compareTo(Hand o) {
            Rank ourRank = this.rank();
            Rank otherRank = o.rank();

            int result = ourRank.compareTo(otherRank);

            if (result == 0) {
                for (int i=0; i < 5; i++) {
                    result = compareCard(cards[i], o.cards[i]);
                    if (result != 0) return result;
                }
            }
            return result;
        }
    }

    static int compareCard(Character card, Character other) {
        Integer thisValue = cardValues.get(card);
        Integer otherValue = cardValues.get(other);
        return thisValue.compareTo(otherValue);
    }

    static Map<Character, Integer> cardValues = new HashMap<>();
    static {
        cardValues.put('2', 2);
        cardValues.put('3', 3);
        cardValues.put('4', 4);
        cardValues.put('5', 5);
        cardValues.put('6', 6);
        cardValues.put('7', 7);
        cardValues.put('8', 8);
        cardValues.put('9', 9);
        cardValues.put('T', 10);
        cardValues.put('J', 1);     // part 2, now joker
        cardValues.put('Q', 12);
        cardValues.put('K', 13);
        cardValues.put('A', 14);
    }

    @RequiredArgsConstructor
    enum Rank {
        ONE_OF_A_KIND(1),
        PAIR(2),
        TWO_PAIRS(3),
        THREE_OF_A_KIND(3),
        FULL_HOUSE(4),
        FOUR_OF_A_KIND(5),
        FIVE_OF_A_KIND(6);
        final int value;
    }
}
