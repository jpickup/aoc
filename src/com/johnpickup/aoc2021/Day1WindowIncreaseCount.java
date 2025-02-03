package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day1WindowIncreaseCount {
    public static void main(String[] args) throws Exception {
        int increaseCount = 0;
        Optional<Integer> last = Optional.empty();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/Users/john/Development/AdventOfCode/resources/Day1Input.txt"))) {
            List<Integer> integers = stream.map(Integer::parseInt).collect(Collectors.toList());
            List<Integer> windows = new ArrayList<>();
            for (int i = 2; i < integers.size(); i++) {
                windows.add(integers.get(i) + integers.get(i - 1) + integers.get(i - 2));
            }

            for (Integer integer : windows) {
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
