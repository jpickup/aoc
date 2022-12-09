package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6LanternFish {
    public static void main(String[] args) throws Exception {

        try (Stream<String> stream = Files.lines(Paths.get(
                "/Users/john/Development/AdventOfCode/resources/Day6Input.txt"))) {

            String firstLine = stream.findFirst().get();
            String[] inputs = firstLine.split(",");
            List<Fish> population = new ArrayList<>(Arrays.stream(inputs).map(Day6LanternFish::parseFish).filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            printPopulation(population);

            for (int time=0; time < 256; time ++) {
                List<Fish> nextGeneration = new ArrayList<>();
                for (Fish fish : population) {
                    nextGeneration.addAll(fish.tick());
                }
                population = nextGeneration;
                //printPopulation(population);

            }

            System.out.printf("Number of fish : %d", population.size());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printPopulation(List<Fish> population) {
        for (Fish fish : population) {
            System.out.print(fish.timer + " ");
        }
        System.out.println();
    }

    static Fish parseFish(String input) {
        if (input != null && !input.isEmpty()) return new Fish(input);
        return null;
    }


    static class Fish {
        int timer;

        Fish(int timer) {
            this.timer = timer;
        }

        Fish(String input) {
            this(Integer.parseInt(input));
        }

        List<Fish> tick() {
            timer--;
            if (timer < 0) {
                timer = 6;
                return Arrays.asList(this, new Fish(8));
            }
            else {
                return Collections.singletonList(this);
            }

        }
    }
}
