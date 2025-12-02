package com.johnpickup.aoc2025;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day1 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<Rotation> rotations = stream
                        .filter(s -> !s.isEmpty())
                        .map(Rotation::new)
                        .toList();

                State state = new State();
                for (Rotation rotation : rotations) {
                    state = state.apply(rotation);
                }

                System.out.println(state);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @Getter
    static class State {
        private static final int SIZE = 100;
        private final int position;
        private final int part1;
        private final int part2;
        State(int position, int part1, int part2) {
            this.position = position;
            this.part1 = part1;
            this.part2 = part2;
        }

        State() {
            this(50, 0, 0);
        }

        State apply(Rotation rotation) {
            int newPosition =  position + rotation.getSign() * rotation.getSteps();
            int rotations = 0;
            while (newPosition < 0) {
                newPosition += SIZE;
                rotations++;
            }
            while (newPosition >= SIZE) {
                newPosition -= SIZE;
                rotations++;
            }
            return new State(newPosition, part1 + (newPosition==0?1:0), part2 + rotations);
        }

        @Override
        public String toString() {
            return "Part 1: " + part1 + ", " + "Part 2: " + part2;
        }
    }

    @Getter
    static class Rotation {
        final char direction;
        final int steps;
        Rotation(String s) {
            direction = s.charAt(0);
            steps = Integer.parseInt(s.substring(1));
            if (direction != 'L' && direction != 'R') throw new RuntimeException("Invalid direction " + s);
        }

        int getSign() {
            return direction=='L'?-1:+1;
        }
    }
}
