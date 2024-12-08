package com.johnpickup.aoc2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day5.txt"))) {
            List<BoardingCard> boardingCards = stream.filter(s -> !s.isEmpty()).map(BoardingCard::new).collect(Collectors.toList());
            //boardingCards.forEach(System.out::println);
            int min = boardingCards.stream().map(BoardingCard::seatId).min(Integer::compareTo).get();
            int max = boardingCards.stream().map(BoardingCard::seatId).max(Integer::compareTo).get();
            System.out.println("Part 1 : " + max);

            Set<Integer> seatIds = boardingCards.stream().map(BoardingCard::seatId).collect(Collectors.toSet());
            for (int i = min; i <= max; i++) {
                if (seatIds.contains(i-1) && !seatIds.contains(i) && seatIds.contains(i+1))
                    System.out.println("Possible Part 2 : " + i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    static class BoardingCard {
        final String input;
        final int row;
        final int column;
        BoardingCard(String line) {
            input = line;
            String rowParts = line.substring(0, 7);
            String colParts = line.substring(7);

            row = fromBinary(rowParts, 'F', 'B');
            column = fromBinary(colParts, 'L', 'R');
        }

        public String toString() {
            return String.format("%s: row %d, column %d, seat ID %d", input, row, column, seatId());
        }

        private int fromBinary(String s, char lower, char upper) {
            int result = 0;
            int v = 1;
            for (int i = s.length()-1; i >=0; i--) {
                if (s.charAt(i) == upper) result += v;
                v = v * 2;
            }
            return result;
        }

        int seatId() {
            return row * 8 + column;
        }

    }
}
