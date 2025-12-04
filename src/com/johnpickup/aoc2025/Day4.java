package com.johnpickup.aoc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.johnpickup.util.FileUtils.getInputFilenames;

public class Day4 {
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
                        .toList();

                PaperGrid paperGrid = new PaperGrid(lines);
                System.out.println("Part 1: " + paperGrid.part1());
                System.out.println("Part 2: " + paperGrid.part2());
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    private static class PaperGrid {

        private static final char PAPER = '@';
        private static final char SPACE = '.';

        private final CharGrid grid;

        public PaperGrid(CharGrid charGrid) {
            grid = new CharGrid(charGrid);
        }

        public PaperGrid(List<String> lines) {
            grid = new CharGrid(lines);
        }

        public long part1() {
            return accessible().size();
        }

        private List<Coord> accessible() {
            List<Coord> result = new ArrayList<>();
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    Coord c = new Coord(x,y);
                    if (isAccessible(c)) {
                        result.add(c);
                    }
                }
            }
            return result;
        }

        private boolean isAccessible(Coord c) {
            if (grid.getCell(c) == SPACE) return false;
            return countAdjacent(c) < 4;
        }

        private int countAdjacent(Coord c) {
            int adjcents = 0;
            if (grid.getCell(c.north()) == PAPER) adjcents++;
            if (grid.getCell(c.northEast()) == PAPER) adjcents++;
            if (grid.getCell(c.east()) == PAPER) adjcents++;
            if (grid.getCell(c.southEast()) == PAPER) adjcents++;
            if (grid.getCell(c.south()) == PAPER) adjcents++;
            if (grid.getCell(c.southWest()) == PAPER) adjcents++;
            if (grid.getCell(c.west()) == PAPER) adjcents++;
            if (grid.getCell(c.northWest()) == PAPER) adjcents++;
            return adjcents;
        }

        public long part2() {
            List<Coord> accessible = accessible();
            if (accessible.isEmpty()) return 0;
            return accessible.size() + without(accessible).part2();
        }

        private PaperGrid without(List<Coord> cs) {
            PaperGrid result = new PaperGrid(grid);
            for (Coord c : cs) {
                result.grid.setCell(c, SPACE);
            }
            return result;
        }
    }
}
