package com.johnpickup.aoc2019;

import com.johnpickup.util.Coord;
import com.johnpickup.util.Direction;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day11 {
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

                Robot robot = new Robot(lines.get(0));
                robot.setFirstColour(Robot.BLACK);
                robot.execute();

                long part1 = robot.part1();
                System.out.println("Part 1: " + part1);

                robot.reset();
                robot.setFirstColour(Robot.WHITE);
                robot.execute();
                String part2 = robot.render();
                System.out.println("Part 2: \n" + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Robot {
        private static final int BLACK = 0;
        private static final int WHITE = 1;
        private static final int DEFAULT_COLOUR = BLACK;
        @Setter
        int firstColour;
        final Program program;
        final Map<Coord, Integer> cells;

        Direction direction;
        Coord location;
        OutputState outputState;
        boolean firstRead = true;


        Robot(String line) {
            program = new Program(line);
            cells = new HashMap<>();
            program.setInputSupplier(this::currentCellColour);
            program.setOutputConsumer(this::consumeOutput);
            reset();
        }

        void reset() {
            cells.clear();
            program.reset();
            direction = Direction.NORTH;
            location = Coord.ORIGIN;
            outputState = OutputState.COLOUR;
            firstRead = true;
        }

        long part1() {
            return cells.size();
        }

        long currentCellColour() {
            if (firstRead) {
                firstRead = false;
                return firstColour;
            }
            return getCell(location);
        }

        void consumeOutput(long value) {
            switch (outputState) {
                case COLOUR:
                    setCell(location, (int)value);
                    outputState = OutputState.TURN_DIRECTION;
                    break;
                case TURN_DIRECTION:
                    switch ((int)value) {
                        case 0 : direction = direction.left(); break;
                        case 1: direction = direction.right(); break;
                        default: throw new RuntimeException("Unexpected output " + value);
                    }
                    location = location.move(direction, 1);
                    outputState = OutputState.COLOUR;
                    break;
                default: throw  new RuntimeException("Unknown state");
            }
        }

        int getCell(Coord coord) {
            cells.putIfAbsent(coord, DEFAULT_COLOUR);
            return cells.get(coord);
        }

        void setCell(Coord coord, int colour) {
            cells.put(coord, colour);
        }

        public void execute() {
            program.execute();
        }

        public String render() {
            int minX = cells.keySet().stream().map(Coord::getX).min(Integer::compareTo).get();
            int maxX = cells.keySet().stream().map(Coord::getX).max(Integer::compareTo).get();
            int minY = cells.keySet().stream().map(Coord::getY).min(Integer::compareTo).get();
            int maxY = cells.keySet().stream().map(Coord::getY).max(Integer::compareTo).get();
            StringBuilder sb = new StringBuilder();
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    sb.append(getCell(new Coord(x, y)) == 1 ? '#' : ' ');
                }
                sb.append('\n');
            }
            return sb.toString();
        }
    }

    enum OutputState {
        COLOUR,
        TURN_DIRECTION
    }
}
