package com.johnpickup.aoc2025;

import com.johnpickup.util.CharGrid;
import com.johnpickup.util.Coord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            return grid.allCells()
                    .filter(this::isAccessible)
                    .toList();
        }

        private boolean isAccessible(Coord c) {
            if (grid.getCell(c) == SPACE) return false;
            return countAdjacent(c) < 4;
        }

        private long countAdjacent(Coord c) {
            return c.adjacent8().stream()
                    .filter(a -> grid.getCell(a) == PAPER)
                    .count();
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
