package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1IncreaseCount {
    public static void main(String[] args) throws Exception {
        int increaseCount = 0;
        Optional<Integer> last = Optional.empty();
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/Day1Input.txt"))) {
            List<Integer> integers = stream.map(Integer::parseInt).collect(Collectors.toList());

            for (Integer integer : integers) {
                if (last.isPresent() && integer > last.get()) {
                    increaseCount++;
                }
                last = Optional.of(integer);
            }

            System.out.println(increaseCount);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
