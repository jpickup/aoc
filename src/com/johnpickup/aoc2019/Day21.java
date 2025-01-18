package com.johnpickup.aoc2019;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day21 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + ".txt"
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

                SpringDroid droid = new SpringDroid(lines.get(0));
                System.out.println("Part 1: " + droid.part1());
                System.out.println("Part 2: " + droid.part2());
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class SpringDroid {
        final Program program;

        SpringDroid(String line) {
            program = new Program(line);
        }

        public long part1() {
            return runSpringScript(createScriptPart1());
        }

        public long part2() {
            return runSpringScript(createScriptPart2());
        }

        private long runSpringScript(List<String> script) {
            try {
                String springScript = String.join("\n", script)+"\n";
                program.reset();
                program.addInputs(stringToAscii(springScript));
                program.execute();
            } catch (Program.MissingInputException ex) {
                System.err.println("Missing input" + ex);
            }
            System.out.println(asciiToString(program.outputs));
            System.out.println(program.outputs);
            return program.outputs.get(program.outputs.size()-1);
        }

        private List<String> createScriptPart1() {
            // a jump goes forward 4 squares
            // we need to land on solid ground
            // if any of the next 3 squares are holes and the 4th is solid then jump
            return Arrays.asList(
                    "NOT A J",
                    "NOT B T",
                    "OR T J",
                    "NOT C T",
                    "OR T J",
                    "AND D J",
                    "WALK"
            );
        }

        private List<String> createScriptPart2() {
            // a jump goes forward 4 squares
            // BUT we need to know that where we land can be jump-able - don't want to jump too soon
            // e.g.
            //.................
            //.................
            //..@..............
            //#####.#.##.##.###
            // here jumping at 1st opportunity fails as where we land is a place we can't jump from
            // correct jumps are:
            //    1   2   3
            //#####.#.##.##.###
            return Arrays.asList(
                    "NOT A J",
                    "NOT B T",
                    "OR T J",
                    "NOT C T",
                    "OR T J",
                    "NOT E T",
                    "OR T J",
                    "NOT F T",
                    "OR T J",
                    "NOT G T",
                    "OR T J",
                    "AND D J",
                    "AND H J",
                    "RUN"
            );
        }

        private String asciiToString(List<Long> values) {
            StringBuilder sb = new StringBuilder();
            values.stream().filter(l -> l < 256).map(l -> (char)(l%256)).forEach(sb::append);
            return sb.toString();
        }

        private List<Long> stringToAscii(String s) {
            List<Long> result = new ArrayList<>();
            for (int i = 0 ; i < s.length(); i++) {
                int ch = s.charAt(i);
                result.add((long)ch);
            }
            return result;
        }
    }

}
