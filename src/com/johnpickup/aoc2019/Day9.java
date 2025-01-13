package com.johnpickup.aoc2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day9 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + "-test.txt"
                , prefix + "-test2.txt"
                , prefix + "-test3.txt"
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

                Program program = new Program(lines.get(0));

                if (!isTest) program.addInput(1L);
                program.execute();

                System.out.println(program.showOutput());
                System.out.println("Part 1: " + program.getOutput(0));

                program.reset();
                if (!isTest) program.addInput(2L);
                program.execute();

                System.out.println(program.showOutput());
                System.out.println("Part 2: " + program.getOutput(0));

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

}
