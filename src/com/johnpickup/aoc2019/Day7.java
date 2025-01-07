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
        String day = new Object() {
        }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                // part 1
                //prefix + "-test.txt"
                //, prefix + "-test2.txt"
                //, prefix + "-test3.txt"
                // part 2
                //prefix + "-test4.txt"
                //, prefix + "-test5.txt"
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            createEmptyTestFileIfMissing(inputFilename);
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            List<List<Integer>> possible04Phases = Combinations.allPossiblePermutations(5, Arrays.asList(0, 1, 2, 3, 4));
            List<List<Integer>> possible59Phases = Combinations.allPossiblePermutations(5, Arrays.asList(5, 6, 7, 8, 9));
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Amplifiers amplifiers = new Amplifiers(lines.get(0));

                long maxOutput = 0;
                List<Integer> maxSequence = null;
                // Part 1 --------------------------
                for (List<Integer> possiblePhase : possible04Phases) {
                    long output = amplifiers.testPhases(possiblePhase);
                    if (output > maxOutput) {
                        maxOutput = output;
                        maxSequence = possiblePhase;
                    }
                }
                System.out.println("Part 1: " + maxSequence.toString() + " -> " + maxOutput);

                // Part 2 --------------------------
                maxOutput = 0;
                maxSequence = null;
                for (List<Integer> possiblePhase : possible59Phases) {
                    List<Long> outputs = amplifiers.testPhases2(possiblePhase);
                    if (outputs.get(outputs.size()-1) > maxOutput) {
                        maxOutput = outputs.get(outputs.size()-1);
                        maxSequence = possiblePhase;
                    }
                }

                System.out.println("Part 2: " + maxSequence.toString() + " -> " + maxOutput);

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
            for (int i = 0; i < 5; i++) {
                amplifiers.add(new Amplifier(line));
            }
        }

        public long testPhases(List<Integer> possiblePhase) {
            if (possiblePhase.size() != amplifiers.size()) throw new RuntimeException("Incorrect number of phases");
            for (int i = 0; i < 5; i++) {
                amplifiers.get(i).program.reset();
                amplifiers.get(i).setPhase(possiblePhase.get(i));
            }

            long lastOutput = 0;
            for (int i = 0; i < 5; i++) {
                amplifiers.get(i).setInputSignal(lastOutput);
                amplifiers.get(i).program.execute();
                lastOutput = amplifiers.get(i).program.outputs.get(0);
            }
            return lastOutput;
        }

        public List<Long> testPhases2(List<Integer> possiblePhase) {
            if (possiblePhase.size() != amplifiers.size()) throw new RuntimeException("Incorrect number of phases");
            for (int i = 0; i < 5; i++) {
                amplifiers.get(i).program.reset();
                amplifiers.get(i).setPhase(possiblePhase.get(i));
            }

            List<Long> outputs = new ArrayList<>();
            outputs.add(0L);
            int ampIdx = 0;
            while (!allTerminated()) {
                amplifiers.get(ampIdx).setInputSignals(outputs);
                try {
                    amplifiers.get(ampIdx).program.execute();
                } catch (Program.MissingInputException ex) {
                    // NOOP
                }
                outputs = amplifiers.get(ampIdx).program.consumeOutputs();
                ampIdx = (ampIdx + 1) % amplifiers.size();
            }
            return outputs;
        }

        private boolean allTerminated() {
            return amplifiers.stream().map(Amplifier::isTerminated).reduce(true, (a, b) -> a & b);
        }

        public void reset() {
            for (int i = 0; i < 5; i++) {
                amplifiers.get(i).reset();
            }
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

        public void setInputSignal(long input) {
            program.addInput(input);
        }

        public void reset() {
            program.reset();
        }

        public boolean isTerminated() {
            return program.isTerminated();
        }

        public void setInputSignals(List<Long> outputs) {
            outputs.forEach(program::addInput);
        }
    }
}
