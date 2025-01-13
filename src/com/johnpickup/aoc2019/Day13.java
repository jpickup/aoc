package com.johnpickup.aoc2019;

import com.johnpickup.util.Coord;
import com.johnpickup.util.SparseGrid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.createEmptyTestFileIfMissing;

public class Day13 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/User Data/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                //prefix + "-test.txt"
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

                ArcadeCabinet arcadeCabinet = new ArcadeCabinet(lines.get(0));
                arcadeCabinet.execute();
                System.out.println(arcadeCabinet);
                long part1 = arcadeCabinet.part1();
                System.out.println("Part 1: " + part1);

                arcadeCabinet.reset();
                arcadeCabinet.insertCoins(2);
                arcadeCabinet.execute();
                long part2 = arcadeCabinet.getScore();
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class ArcadeCabinet {
        final Program program;
        final SparseGrid<Integer> screen;
        private Joystick joystick;
        @Getter
        private long score = 0;
        private final Coord scoreCoord = new Coord(-1,0);
        private Coord ballPosition = Coord.ORIGIN;
        private Coord paddlePosition = Coord.ORIGIN;

        ArcadeCabinet(String line) {
            program = new Program(line);
            program.inputSupplier = this::supplyInput;
            program.outputConsumer = this::processOutput;
            screen = new SparseGrid<>();
        }

        public void reset() {
            program.reset();
            joystick = Joystick.MIDDLE;
        }

        public void execute() {
            program.execute();
        }

        @Override
        public String toString() {
            return screen.toString();
        }

        private long supplyInput() {
            return joystick.position;
        }

        private final List<Integer> outputQueue = new ArrayList<>();
        private void processOutput(long output) {
            outputQueue.add((int)output);
            if (outputQueue.size() == 3) {
                int x = outputQueue.get(0);
                int y = outputQueue.get(1);
                int t = outputQueue.get(2);
                outputQueue.remove(2);
                outputQueue.remove(1);
                outputQueue.remove(0);
                Coord coord = new Coord(x, y);
                if (coord.equals(scoreCoord)) {
                    score = t;
                } else {
                    screen.setCell(coord, t);
                    switch (t) {
                        case 3: paddlePosition = coord; break;
                        case 4: ballPosition = coord; break;
                    }
                }
                joystick = Joystick.MIDDLE;
                if (ballPosition.getX() < paddlePosition.getX()) joystick = Joystick.LEFT;
                if (ballPosition.getX() > paddlePosition.getX()) joystick = Joystick.RIGHT;
            }
        }

        public long part1() {
            return screen.getCells().values().stream().filter(v -> v == 2).count();
        }

        public void insertCoins(int number) {
            program.setMemory(0, number);
        }
    }

    @RequiredArgsConstructor
    enum Joystick {
        LEFT(-1),
        MIDDLE(0),
        RIGHT(1);
        final long position;
    }
}
