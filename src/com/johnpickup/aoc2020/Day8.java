package com.johnpickup.aoc2020;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day8 {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        try (Stream<String> stream = Files.lines(Paths.get("/Volumes/User Data/john/Development/AdventOfCode/resources/2020/Day8/Day8.txt"))) {
            List<Instruction> instructions = stream.filter(s -> !s.isEmpty()).map(Instruction::new).collect(Collectors.toList());

            System.out.println(instructions);
            long part1 = Instruction.part1();
            System.out.println("Part 1: " + part1);
            long part2 = Instruction.part2();
            System.out.println("Part 2: " + part2);

        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("Time: " + (end - start) + "ms");
    }

    @ToString
    @RequiredArgsConstructor
    static class Instruction {
        static final List<Instruction> loadedInstructions = new ArrayList<>();
        final Operation operation;
        final int value;
        Instruction(String line) {
            String[] parts = line.split(" ");
            operation = Operation.fromString(parts[0]);
            value = Integer.parseInt(parts[1]);
            loadedInstructions.add(this);
        }

        static int part1() {
            State state = execute(loadedInstructions);
            return state.acc;
        }

        static State execute(List<Instruction> instructions) {
            State state = new State(0,0, false);
            List<Integer> executed = new ArrayList<>();

            while (!executed.contains(state.ip) && !state.terminated) {
                executed.add(state.ip);
                Instruction instruction = instructions.get(state.ip);
                state = instruction.execute(state);
            }
            return state;
        }

        private State execute(State state) {
            switch (operation) {
                case NOP: return new State(state.acc, state.ip+1, state.ip+1 >= loadedInstructions.size());
                case ACC: return new State(state.acc + value, state.ip+1, state.ip+1 >= loadedInstructions.size());
                case JMP: return new State(state.acc, state.ip+value, state.ip+value >= loadedInstructions.size());
                default: throw new RuntimeException("Unknown operation");
            }
        }

        static int part2() {
            for (int i = 0; i < loadedInstructions.size(); i++) {
                List<Instruction> modifiedInstructions = new ArrayList<>(loadedInstructions);
                Instruction instruction = loadedInstructions.get(i);
                switch (instruction.operation) {
                    case ACC: continue;
                    case JMP:
                        modifiedInstructions.remove(i);
                        modifiedInstructions.add(i, new Instruction(Operation.NOP, instruction.value));
                        break;
                    case NOP:
                        modifiedInstructions.remove(i);
                        modifiedInstructions.add(i, new Instruction(Operation.JMP, instruction.value));
                        break;
                }
                State result = execute(modifiedInstructions);
                if (result.terminated) return result.acc;
            }

            throw new RuntimeException("Never terminated");
        }
    }
    enum Operation {
        ACC,
        JMP,
        NOP;

        static Operation fromString(String s) {
            switch (s) {
                case "acc": return ACC;
                case "jmp": return JMP;
                case "nop": return NOP;
                default: throw new RuntimeException("Unknown " + s);
            }
        }
    }

    @RequiredArgsConstructor
    static class State {
        final int acc;
        final int ip;
        final boolean terminated;
    }
}
