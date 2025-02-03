package com.johnpickup.aoc2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 {
    public static void main(String[] args) throws Exception {
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/2022/Day1.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());
            int total = 0;
            List<Integer> elves = new ArrayList<>();

            for (String line : lines) {
                if (line.length()==0) {
                    elves.add(total);
                    total = 0;
                } else {
                    total += Integer.parseInt(line);
                }
            }
            elves.add(total);
            elves.sort((integer, anotherInteger) -> -integer.compareTo(anotherInteger));

            System.out.println(elves);
            System.out.println(elves.get(0)+elves.get(1)+elves.get(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
