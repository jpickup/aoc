package com.johnpickup.aoc2019;

import com.johnpickup.common.Combinations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day7 {
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

                Amplifiers amplifiers = new Amplifiers(lines.get(0));

                List<List<Integer>> possiblePhases = Combinations.allPossiblePermutations(5, Arrays.asList(0, 1, 2, 3, 4));

                int maxOutput = 0;
                List<Integer> maxSequence = null;
                for (List<Integer> possiblePhase : possiblePhases) {
                    int output = amplifiers.testPhases(possiblePhase);
                    if (output > maxOutput) {
                        maxOutput = output;
                        maxSequence = possiblePhase;
                    }
                }


                System.out.println("Part 1: " + maxSequence.toString() + " -> " + maxOutput);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Amplifiers {
        final List<Amplifier> amplifiers;
        Amplifiers(String line) {
            amplifiers = new ArrayList<>();
            for (int i = 0 ; i < 5; i++) {
                amplifiers.add(new Amplifier(line));
            }
        }

        public int testPhases(List<Integer> possiblePhase) {
            if (possiblePhase.size() != amplifiers.size()) throw new RuntimeException("Incorrect number of phases");
            for (int i = 0 ; i < 5; i++) {
                amplifiers.get(i).program.reset();
                amplifiers.get(i).setPhase(possiblePhase.get(i));
            }

            int lastOutput = 0;
            for (int i = 0 ; i < 5; i++) {
                amplifiers.get(i).setInputSignal(lastOutput);
                amplifiers.get(i).program.execute();
                lastOutput = amplifiers.get(i).program.outputs.get(0);
            }
            return lastOutput;
        }
    }

    static class Amplifier {
        final Program program;
        Amplifier(String line) {
            program = new Program(line);
        }

        void setPhase(int phase) {
            program.addInput(phase);
        }

        public void setInputSignal(int input) {
            program.addInput(input);
        }
    }
}
