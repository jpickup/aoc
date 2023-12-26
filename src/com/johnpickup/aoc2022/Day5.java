package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
    static int part = 2;
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day5.txt"))) {
            List<String> lines = stream.collect(Collectors.toList());

            List<Stack<Character>> stacks = new ArrayList<>();

            boolean readingStacks = true;
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    readingStacks = false;
                    System.out.println("Stacks:" + stacks);
                }
                else {
                    if (readingStacks) {
                        List<Location> locations = parseStack(line);
                        for (Location location : locations) {
                            while (stacks.size() <= location.stackIndex) {
                                stacks.add(new Stack<>());
                            }
                            stacks.get(location.stackIndex).add(0,location.letter);
                        }
                    }
                    else {
                        Instruction instruction = parseInstruction(line);
                        executeInstruction(instruction, stacks);
                        System.out.println("Stacks:" + stacks);
                    }
                }
            }
            for (Stack<Character> stack : stacks) {
                System.out.print(stack.peek());
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executeInstruction(Instruction instruction, List<Stack<Character>> stacks) {
        if (part == 1) {
            for (int i = 0; i < instruction.count; i++) {
                stacks.get(instruction.to - 1).push(stacks.get(instruction.from - 1).pop());
            }
        }
        else {
            Stack<Character> temp = new Stack<>();
            for (int i = 0; i < instruction.count; i++) {
                temp.push(stacks.get(instruction.from - 1).pop());
            }
            while (!temp.isEmpty()) {
                stacks.get(instruction.to - 1).push(temp.pop());
            }
        }
    }

    private static List<Location> parseStack(String line) {
        List<Location> result = new ArrayList<>();
        System.out.println("Parse stack: " + line);
        for (int i = 0; i < (line.length()+1)/4; i++) {
            char c = line.charAt(i * 4 + 1);
            if (c!=' ') result.add(Location.builder().stackIndex(i).letter(c).build());
        }
        System.out.println(result);
        return result;
    }

    private static Instruction parseInstruction(String line) {
        System.out.println("Parse instruction: " + line);
        String[] tokens = line.split(" ");

        Instruction instruction = Instruction.builder()
                .count(Integer.parseInt(tokens[1]))
                .from(Integer.parseInt(tokens[3]))
                .to(Integer.parseInt(tokens[5]))
                .build();
        System.out.println(instruction);
        return instruction;
    }

    @Data
    @Builder
    static class Location {
        Character letter;
        int stackIndex;
    }

    @Data
    @Builder
    static class Instruction {
        int count;
        int from;
        int to;
    }
}
