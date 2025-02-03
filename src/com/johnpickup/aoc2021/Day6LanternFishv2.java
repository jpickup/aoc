package com.johnpickup.aoc2021;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6LanternFishv2 {
    public static void main(String[] args) throws Exception {

        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day6Input.txt"))) {

            String firstLine = stream.findFirst().get();
            String[] inputs = firstLine.split(",");
            List<Integer> population = new ArrayList<>(Arrays.stream(inputs).map(Integer::parseInt).filter(Objects::nonNull)
                    .collect(Collectors.toList()));

            BigInteger[] timers = new BigInteger[9];

            for (int timer = 0; timer < 9; timer ++) {
                timers[timer] = BigInteger.ZERO;
            }
            for (Integer fish : population) {
                timers[fish] = timers[fish].add(BigInteger.ONE);
            }

            printTimers(timers);

            for (int time=0; time < 256; time ++) {
                BigInteger[] newTimers = new BigInteger[9];
                for (int timer = 0; timer < 8; timer ++) {
                    newTimers[timer] = timers[timer+1];
                }
                newTimers[8] = timers[0];
                newTimers[6] = newTimers[6].add(timers[0]);

                timers = newTimers;
                printTimers(timers);

            }

            BigInteger populationSize = BigInteger.ZERO;
            for (int timer = 0; timer < 9; timer ++) {
                populationSize = populationSize.add(timers[timer]);
            }

            System.out.printf("Number of fish : %s", populationSize.toString());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printTimers(BigInteger[] timers) {
        for (int i = 0; i < 9; i++) {
            System.out.print(timers[i] + " ");
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
