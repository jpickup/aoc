package com.johnpickup.aoc2024;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 {
    static boolean part1 = false;
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        List<Instruction> instructions;
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2024/Day3/Day3.txt"))) {
            String input = stream.filter(s -> !s.isEmpty()).collect(Collectors.toList()).stream().reduce("", (a,b) -> a + " " + b);
            instructions = extractInstructions(input);
            Long result = instructions.stream().map(Instruction::value).reduce(0L, Long::sum);
            System.out.println("Result: " + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }


    private static List<Instruction> extractInstructions(String input) {
        List<Instruction> instructions = new ArrayList<>();
        Pattern mulPattern = Pattern.compile("(mul)\\(([0-9]+)\\,([0-9]+)\\).*");
        boolean enabled = true;
        for (int i = 0; i < input.length(); i++) {
            String remaining = input.substring(i);
            if (remaining.startsWith("do()"))
                enabled = true;
            else if (remaining.startsWith("don't()"))
                enabled = false;
            else {
                Matcher matcher = mulPattern.matcher(remaining);
                if (matcher.matches()) {
                    System.out.println();
                    Instruction instruction = new Instruction(matcher.group(1), matcher.group(2), matcher.group(3));
                    if(enabled || part1) {
                        instructions.add(instruction);
                    }
                }
            }
        }
        return instructions;
    }

    @RequiredArgsConstructor
    @Data
    static class Instruction {
        private final String operation;     // wasn't needed in the end
        private final Long left;
        private final Long right;

        public Instruction(String op, String arg1, String arg2) {
            this(op, Long.parseLong(arg1), Long.parseLong(arg2));
        }

        public long value() {
            if (operation.equals("mul"))
                return left * right;
            else
                return 0L;
        }
    }
}
