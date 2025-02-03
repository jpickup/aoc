package com.johnpickup.aoc2019;

import com.johnpickup.util.Coord;
import com.johnpickup.util.SparseGrid;
import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class Day19 {
    static boolean isTest;
    public static void main(String[] args) {
        String day = new Object() { }.getClass().getEnclosingClass().getSimpleName();
        String prefix = "/Volumes/Users/john/Development/AdventOfCode/resources/2019/" + day + "/" + day;
        List<String> inputFilenames = Arrays.asList(
                prefix + ".txt"
        );
        for (String inputFilename : inputFilenames) {
            
            long start = System.currentTimeMillis();
            System.out.println(inputFilename);
            isTest = inputFilename.contains("test");
            try (Stream<String> stream = Files.lines(Paths.get(inputFilename))) {
                List<String> lines = stream
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

                Drone drone = new Drone(lines.get(0));

                long part1 = drone.part1();
                System.out.println("Part 1: " + part1);
                long part2 = drone.part2();
                System.out.println("Part 2: " + part2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            long end = System.currentTimeMillis();
            System.out.println("Time: " + (end - start) + "ms");
        }
    }

    static class Drone {
        final Program program;
        final SparseGrid<Long> grid;
        Drone(String input) {
            program = new Program(input);
            grid = new SparseGrid<>();
        }

        long part1() {
            scan(50);
            System.out.println(grid);
            return grid.findCells(1L).size();
        }

        long part2() {
            long result = 0L;
            Map<Integer, RowData> rowData = new HashMap<>();

            int currRow = 100;
            rowData.put(currRow, calcRowData(100));
            while (result == 0L) {
                int start = rowData.get(currRow).start;
                int end = rowData.get(currRow).end;
                currRow++;
                start-=1; end-=1;       // show some empty & full either side
                while (scanCell(new Coord(start, currRow)) == 0L) start++;
                while (scanCell(new Coord(end, currRow)) == 1L) end++;
                rowData.put(currRow, new RowData(currRow, start, end-1));
                RowData topRow = rowData.get(currRow - 99);
                if (topRow != null) {
                    RowData bottomRow = rowData.get(currRow);
                    Coord topLeft = new Coord(bottomRow.start, topRow.row);
                    Coord bottomRight = new Coord(topRow.end, bottomRow.row);
                    if ((bottomRight.getX() - topLeft.getX() == 99) && (bottomRight.getY() - topLeft.getY() == 99)) {
                        result = topLeft.getX() * 10000L + topLeft.getY();
                    }
                }
            }
            //System.out.println(grid);
            return result;
        }

// Trig approach - didn't work
//            double theta = 1d * rowData.start / calcRow;
//            double phi = 1d * rowData.end / calcRow;
//
//            double y = (Math.tan(theta)*100 + 100) / (Math.tan(phi) - Math.tan(theta));
//            double x = Math.tan(theta) * (y + 100);
//
//            int intX = (int)x;
//            int intY = (int)y;
//

        private RowData calcRowData(int row) {
            scanRow(row);
            return getRowData(row);
        }

        private RowData getRowData(int row) {
            int start = 0;
            int end = 0;
            long prev = 0L;
            for (int x = 0; x < row; x++) {
                Coord c = new Coord(x,row);
                Long curr = Optional.ofNullable(grid.getCell(c)).orElse(0L);
                if (curr == 1L && prev == 0L) {
                    start = x;
                }
                if (curr == 0L && prev == 1L) {
                    end = x-1; // inclusive
                }
                prev = curr;
            }
            return new RowData(row, start, end);
        }

       void scan(int size) {
            grid.clear();
            program.reset();
            for (int y = 0; y < size; y++) {
                scanRow(y);
            }
        }

        void scanRange(int minX, int maxX, int minY, int maxY) {
            grid.clear();
            program.reset();
            for (int y = minY; y < maxY; y++) {
                for (int x = minX; x < maxX; x++) {
                    Coord c = new Coord(x,y);
                    scanCell(c);
                }
            }
        }

        void scanRow(int row) {
            program.reset();
            for (int x = 0; x < row; x++) {
                Coord c = new Coord(x,row);
                scanCell(c);
            }
        }

        long scanCell(Coord c) {
            program.reset();
            program.addInput(c.getX());
            program.addInput(c.getY());
            program.execute();
            long output = program.getOutput(0);
            grid.setCell(c, output);
            return output;
        }
    }

    @Data
    static class RowData {
        final int row;
        final int start;
        final int end;

        int length() {return end-start+1;}

        @Override
        public String toString() {
            return String.format("%d : %d .. %d = %d", row, start, end, length());
        }
    }
}
