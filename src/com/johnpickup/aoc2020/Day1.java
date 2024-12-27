package com.johnpickup.aoc2020;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day1/Day1.txt"))) {
            List<Integer> numbers = stream.filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toList());
            System.out.println(numbers);
            for (Integer number1 : numbers) {
                for (Integer number2 : numbers) {
                    for (Integer number3 : numbers) {
                        if (number1 != number2 && number2 != number3 && number1 != number3 && (number1 + number2 + number3) == 2020) {
                            System.out.println(number1 * number2 * number3);
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }
}