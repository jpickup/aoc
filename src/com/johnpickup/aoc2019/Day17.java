package com.johnpickup.aoc2019;

import com.johnpickup.util.Coord;
import com.johnpickup.util.SparseGrid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.aoc2024.util.FileUtils.createEmptyTestFileIfMissing;

public class Day17 {
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
                VacuumRobot robot = new VacuumRobot(lines.get(0));

                long part1 = robot.part1();
                System.out.println("Part 1: " + part1);
                long part2 = 0L;
                System.out.println("Part 2: " + part2);

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class VacuumRobot {
        final Program program;
        final SparseGrid<Character> grid = new SparseGrid<>();
        VacuumRobot(String line) {
            program = new Program(line);
            program.setOutputConsumer(this::outputConsumer);
        }

        Coord position = Coord.ORIGIN;

        private void outputConsumer(long value) {
            if (value == 10L) {
                position = new Coord(0, position.getY()+1);
            } else {
                grid.setCell(position, (char)value);
                position = new Coord(position.getX()+1, position.getY());
            }
        }

        long part1() {
            program.execute();
            System.out.println(grid);
            Set<Coord> intersections = findIntersections(grid);
            System.out.println(intersections);
            return intersections.stream().map(this::alignmentParameter).reduce(0L, Long::sum);
        }

        private long alignmentParameter(Coord coord) {
            return (long) coord.getX() * coord.getY();
        }

        private Set<Coord> findIntersections(SparseGrid<Character> grid) {
            Set<Coord> allScaffold = grid.findCells('#');
            return allScaffold.stream()
                    .filter(c -> isIntersection(c, allScaffold))
                    .collect(Collectors.toSet());
        }

        private boolean isIntersection(Coord coord, Set<Coord> points) {
            return points.contains(coord.north())
                    && points.contains(coord.south())
                    && points.contains(coord.east())
                    && points.contains(coord.west())
                    ;
        }
    }
}
