package com.johnpickup.aoc2020;

import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day23 {
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
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Cups cups = new Cups(lines.get(0));

                String part1 = cups.playGame();
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @ToString
    static class Cups {
        final List<Integer> cups;
        final int lowestCup;
        final int highestCup;
        int currentCup;
        Cups(String line) {
            cups = new ArrayList<>();
            for (char c : line.toCharArray()) {
                cups.add(c-'0');
            }
            currentCup = cups.get(0);
            lowestCup = cups.stream().min(Integer::compareTo).get();
            highestCup = cups.stream().max(Integer::compareTo).get();
        }

        public String playGame() {
            for (int turn=0; turn < 100; turn++) {
                playTurn();
            }
            return labels();
        }

        private String labels() {
            StringBuilder result = new StringBuilder();
            int cupOneIdx = cups.indexOf(1);
            for (int i = cupOneIdx+1; i < cupOneIdx+1+cups.size()-1; i++)
                result.appendCodePoint('0'+getCup(i));
            return result.toString();
        }

        private int getCup(int idx) {
            return cups.get(idx % cups.size());
        }

        private void playTurn() {
            List<Integer> pickedUp = pickUp(3);
            int destinationCup = findDestinationCup((char)(currentCup-1));
            while (pickedUp.contains(destinationCup)) {
                destinationCup = findDestinationCup((char)(destinationCup-1));
            }
            placeCups(destinationCup, pickedUp);
            currentCup = getCup(cups.indexOf(currentCup) + 1);
        }

        private void placeCups(int destinationCup, List<Integer> pickedUp) {
            int destinationIdx = cups.indexOf(destinationCup);
            cups.addAll(destinationIdx+1, pickedUp);
        }

        private int findDestinationCup(int target) {
            return target < lowestCup ? highestCup : target;
        }

        private List<Integer> pickUp(int howMany) {
            List<Integer> result = new ArrayList<>();
            int indexToRemove = (cups.indexOf(currentCup) + 1) % cups.size();
            for (int i = 0; i < howMany ; i++) {
                result.add(getCup(indexToRemove+i));
            }
            for (int cup : result) {
                cups.remove((Integer)cup);
            }
            return result;
        }
    }
}
