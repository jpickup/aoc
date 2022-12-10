package com.johnpickup.aoc2022;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get("/Users/john/Development/AdventOfCode/resources/2022/Day10.txt"))) {
            List<Instruction> instructions = stream.map(Instruction::parse).collect(Collectors.toList());
            System.out.println(instructions);

            Machine state = new Machine();

            for (Instruction instruction : instructions) {
                switch (instruction.command) {
                    case NOOP:
                        state = state.noop();
                        break;
                    case ADDX:
                        state = state.addx(instruction.argument);
                        break;
                    default:
                        throw new RuntimeException("Unknown command " + instruction.command);
                }
            //    System.out.println("State:     " + state);
            }
            System.out.println("--- FINAL ---");
            //System.out.println(state);
            for (int j = 0; j < 6; j++) {
                for (int i=0; i<40; i++) {
                    System.out.print(state.image[i][j]);
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ToString
    static class Machine {
        static final List<Integer> sample = Arrays.asList(20, 60, 100, 140, 180, 220);
        int clock;
        int x;
        long accu;
        char[][] image = new char[40][6];

        Machine() {
            clock = 1;
            x = 1;
            accu = 0;
            for (int i=0; i<40; i++)
                for (int j=0; j<6; j++)
                    image[i][j]='.';
        }

        private void tick() {
            checkCycle();
            render();
            clock++;
        }

        private void render() {
            int row = (clock-1) / 40;
            int col = (clock-1) % 40;
            boolean pixelOn = Math.abs(col - x) <= 1;
            image[col][row] = pixelOn? '#':' ';
        }

        private void checkCycle() {
            if (sample.contains(clock)) {
                accu += (long) clock * x;
            }
        }

        public Machine noop() {
            tick();
            return this;
        }

        public Machine addx(Integer argument) {
            tick();
            tick();
            x += argument;
            return this;
        }
    }

    @RequiredArgsConstructor
    enum Command {
        NOOP(1),
        ADDX(2);
        final int cycles;
    }

    @Builder
    @ToString
    static class Instruction {
        Command command;
        Integer argument;

        public static Instruction parse(String s) {
            String[] parts = s.split(" ");
            return Instruction.builder()
                    .command(Command.valueOf(parts[0].toUpperCase()))
                    .argument(parts.length>1?Integer.parseInt(parts[1]):null)
                    .build();
        }
    }
}
