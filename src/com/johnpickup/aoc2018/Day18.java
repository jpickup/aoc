package com.johnpickup.aoc2018;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day18 {
    static boolean isTest;
    public static void main(String[] args) {
        List<String> inputFilenames = getInputFilenames(new Object(){});
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Map<Lumber, Integer> generationByState = new HashMap<>();
                Map<Integer, Lumber> stateByGeneration = new HashMap<>();

                Lumber lumber = new Lumber(lines);
                generationByState.put(lumber, 0);
                stateByGeneration.put(0, lumber);
                System.out.println(lumber);
                int cycleStart = 0;
                int cycleEnd = 0;
                for (int i = 1; i <= 10000; i++) {
                    lumber = lumber.next();
                    if (generationByState.containsKey(lumber)) {
                        cycleStart = generationByState.get(lumber);
                        cycleEnd = i;
                        System.out.printf("Repeat! %d matches %d %n", i, generationByState.get(lumber));
                        break;
                    }
                    generationByState.put(lumber, i);
                    stateByGeneration.put(i, lumber);
                    System.out.printf("%d -> %d%n", i, lumber.totalResourceValue());
                }

                System.out.println("Part 1: " + lumber.totalResourceValue());

                // can't be bothered to figure out the maths!
                long target = 1000000000L;
                long i = cycleEnd;
                int cycleLength = cycleEnd - cycleStart;
                while (i < 1000000000L) i += cycleLength;
                i -= cycleLength;
                lumber = stateByGeneration.get(cycleStart);
                while (i < target) {
                    lumber = lumber.next();
                    i++;
                }

                System.out.println("Part 2: " + lumber.totalResourceValue());

            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    @EqualsAndHashCode
    static class Lumber {
        static final char OPEN = '.';
        static final char TREES = '|';
        static final char LUMBERYARD = '#';


        final CharGrid grid;
        Lumber(List<String> lines) {
            grid = new CharGrid(lines);
        }

        private Lumber(CharGrid grid) {
            this.grid = grid;
        }

        Lumber next() {
            CharGrid newGrid = new CharGrid(width(), height(), new char[width()][height()]);
            for (int y = 0 ; y < height(); y++) {
                for (int x = 0 ; x < width(); x ++) {
                    Coord coord = new Coord(x, y);
                    newGrid.setCell(coord, determineCell(coord));
                }
            }
            return new Lumber(newGrid);
        }

        @Override
        public String toString() {
            return grid.toString();
        }

        private char determineCell(Coord coord) {
            char curr = grid.getCell(coord);
            List<Character> adjacent = Arrays.asList(
                    grid.getCell(coord.north()),
                    grid.getCell(coord.northEast()),
                    grid.getCell(coord.east()),
                    grid.getCell(coord.southEast()),
                    grid.getCell(coord.south()),
                    grid.getCell(coord.southWest()),
                    grid.getCell(coord.west()),
                    grid.getCell(coord.northWest())
            );

            switch (curr) {
                case OPEN: return countAdjacent(adjacent, TREES) >= 3 ? TREES : OPEN;
                case TREES: return countAdjacent(adjacent, LUMBERYARD) >= 3 ? LUMBERYARD : TREES;
                case LUMBERYARD: return countAdjacent(adjacent, TREES) >= 1 && countAdjacent(adjacent, LUMBERYARD) >= 1 ? LUMBERYARD : OPEN;
                default: return curr;
            }
        }

        private long countAdjacent(List<Character> adjacent, char type) {
            return adjacent.stream().filter(c -> c.equals(type)).count();
        }

        int width() {
            return grid.getWidth();
        }

        int height() {
            return grid.getHeight();
        }

        public long totalResourceValue() {
            return (long)grid.findCells(TREES).size() * (long)grid.findCells(LUMBERYARD).size();
        }
    }
}
