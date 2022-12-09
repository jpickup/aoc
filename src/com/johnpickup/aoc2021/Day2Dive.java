package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2Dive {
    public static void main(String[] args) throws Exception {
        int position = 0;
        int depth = 0;
        int aim = 0;

        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/Day2Input.txt"))) {
            List<Instruction> instructions = stream.map(Day2Dive::parseInstruction)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            for (Instruction instruction : instructions) {
                switch (instruction.command) {
                    case "forward" :
                        position += instruction.value;
                        depth += aim * instruction.value;
                        break;
                    case "backward" :
                        position -= instruction.value;
                        depth -= aim * instruction.value;
                        break;
                    case "down" : aim += instruction.value; break;
                    case "up" : aim -= instruction.value; break;
                    default: System.out.println("Unknown command " + instruction.command);
                }
            }

            System.out.println(position);
            System.out.println(depth);
            System.out.println(position * depth);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Instruction parseInstruction(String s) {
        if (s != null && s.length() > 0) {
            String[] strings = s.split(" ");
            Instruction result = new Instruction();
            result.command = strings[0];
            result.value = Integer.parseInt(strings[1]);
            return result;
        }
        return null;
    }

    static class Instruction {
        String command;
        int value;
    }
}
