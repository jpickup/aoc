package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7Crabs {
    public static void main(String[] args) throws Exception {

        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day7Input.txt"))) {

            String firstLine = stream.findFirst().get();
            String[] inputs = firstLine.split(",");
            List<Integer> crabs = new ArrayList<>(Arrays.stream(inputs).map(Integer::parseInt)
                    .collect(Collectors.toList()));

//            System.out.printf("Number of fish : %d", population.size());
            int total = crabs.stream().reduce(0, Integer::sum);
            int max = crabs.stream().reduce(0, Math::max);
            int count = crabs.size();

            int bestFuel = Integer.MAX_VALUE;
            int bestGuess = 0;

            for (int guess = 0; guess <= max; guess++) {
                int fuel = 0;
                for (int i = 0; i < count; i++) {
                    int diff = Math.abs(crabs.get(i) - guess);
                    int cost = (int)Math.round(diff * diff / 2.0 + diff / 2.0);
                    fuel += cost;
                }
                if (fuel < bestFuel) {
                    bestFuel = fuel;
                    bestGuess = guess;
                }

            }
            System.out.printf("Best Fuel: %d, position: %d", bestFuel, bestGuess);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
