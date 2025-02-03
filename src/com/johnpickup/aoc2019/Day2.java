package com.johnpickup.aoc2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day2 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-test.txt"
                //prefix + "-test2.txt"
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Program program = new Program(lines.get(0));

                System.out.println(program);
                if (!isTest) {
                    program.setMemory(1, 12);
                    program.setMemory(2, 2);
                }
                program.execute();
                long part1 = program.getMemory(0);
                System.out.println("Part 1: " + part1);
                int i=0;
                int j=0;
                long part2 = 0;
                for (i = 0; i < 100; i++) {
                    for (j = 0; j < 100; j++) {
                        program = new Program(lines.get(0));
                        program.setMemory(1, i);
                        program.setMemory(2, j);
                        program.execute();
                        if (program.getMemory(0) == 19690720) {
                            System.out.println(i + "*100 + " + j);
                            part2 = (100L * i) + j;
                            break;
                        }
                    }
                }
                System.out.println("Part 2: " + part2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

}
