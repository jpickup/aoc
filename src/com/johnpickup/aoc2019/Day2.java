package com.johnpickup.aoc2019;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day2 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-test.txt"
                //prefix + "-test2.txt"
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

                Program program = new Program(lines.get(0));

                System.out.println(program);
                if (!isTest) {
                    program.setMemory(1, 12);
                    program.setMemory(2, 2);
                }
                program.execute();
                long part1 = program.part1();
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
                        if (program.part1() == 19690720) {
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

    @ToString
    static class Program {
        final List<Integer> memory;
        Program(String line) {
            memory = new ArrayList<>(Arrays.stream(line.split(",")).map(Integer::parseInt).collect(Collectors.toList()));
        }

        int part1() {
            return memory.get(0);
        }

        void execute() {
            int instructionPointer = 0;
            while (true) {
                Instruction instruction = new Instruction(memory.subList(instructionPointer, memory.size()));
                if (instruction.isTerminate()) break;
                instruction.execute(this);
                instructionPointer += instruction.inputSize();
            }

        }

        public int getMemory(int location) {
            return memory.get(location);
        }

        public void setMemory(int location, int value) {
            memory.remove(location);
            memory.add(location, value);
        }
    }

    @ToString
    static class Instruction {
        final OpCode opCode;
        final List<Integer> parameters;
        Instruction(List<Integer> inputs) {
            opCode = OpCode.parse(inputs.get(0));
            parameters = new ArrayList<>(inputs.subList(1, opCode.paramCount + 1));
        }

        int inputSize() {
            return parameters.size() + 1;
        }

        public boolean isTerminate() {
            return opCode.equals(OpCode.END);
        }

        public void execute(Program program) {
            int arg1 = program.getMemory(parameters.get(0));
            int arg2 = program.getMemory(parameters.get(1));
            int arg3 = parameters.get(2);
            switch(opCode) {
                case ADD:
                    program.setMemory(arg3, arg1 + arg2);
                    break;
                case MULTIPLY:
                    program.setMemory(arg3, arg1 * arg2);
                    break;
                default:
                    throw new RuntimeException("Unknown instruction " + opCode);
            }
        }
    }

    @RequiredArgsConstructor
    enum OpCode {
        ADD(1,3),
        MULTIPLY(1,3),
        END(99,0);

        final int id;
        final int paramCount;

        static OpCode parse(int value) {
            switch (value) {
                case 1: return ADD;
                case 2: return MULTIPLY;
                case 99: return END;
                default: throw new RuntimeException("Unknown OpCode " + value);
            }
        }
    }
}
