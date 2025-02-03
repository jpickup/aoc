package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9Depth {
    public static void main(String[] args) throws Exception {

        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day9Input.txt"))) {
            List<String> lines = stream.filter(Objects::nonNull).collect(Collectors.toList());
            int rows = lines.size();
            int cols = lines.get(0).length();

            int[][] heights = new int[rows][cols];
            int row=0;
            for (String line : lines) {
                for (int col=0; col < line.length(); col++) {
                    heights[row][col] = line.toCharArray()[col] - '0';
                }
                row++;
            }

            printHeights(heights);

            List<Set<Coord>> basins = new ArrayList<>();

            int totalRisk = 0;
            for (row = 0; row < heights.length; row++) {
                for (int col=0; col < heights[row].length; col++) {
                    int current = heights[row][col];
                    int left = col==0?99: heights[row][col-1];
                    int right = col>=cols-1?99: heights[row][col+1];
                    int above = row==0?99 : heights[row-1][col];
                    int below = row>=rows-1?99 : heights[row+1][col];

                    boolean isMinumum = current<left && current < right && current < above && current < below;

                    if (isMinumum) {
                        System.out.println(current);
                        int risk = current + 1;
                        totalRisk += risk;
                    }

                    if (current != 9) {
                        Coord coord = new Coord(row, col);
                        Coord leftCoord = col==0?null:new Coord(row, col-1);
                        Coord rightCoord = col>=cols-1?null:new Coord(row, col+1);
                        Coord aboveCoord = row==0?null:new Coord(row-1, col);
                        Coord belowCoord = row>=rows-1?null:new Coord(row+1, col);

                        boolean found = false;
                        for (Set<Coord> basin : basins) {
                            if (basin.contains(leftCoord) || basin.contains(aboveCoord) || basin.contains(belowCoord) || basin.contains(rightCoord)) {
                                System.out.println("Found existing basin for " + coord);
                                basin.add(coord);
                                if (leftCoord != null && left < 9) basin.add(leftCoord);
                                if (rightCoord != null && right < 9) basin.add(rightCoord);
                                if (aboveCoord != null && above < 9) basin.add(aboveCoord);
                                if (belowCoord != null && below < 9) basin.add(belowCoord);
                                found = true;
                            }
                        }
                        if (!found) {
                            System.out.println("Creating new basin for " + coord);
                            Set<Coord> newBasin = new HashSet<>();
                            newBasin.add(coord);
                            basins.add(newBasin);
                        }
                    }

                }
            }
            System.out.println("TotalRisk: " + totalRisk);

            System.out.println("Initial Basins: " + basins.size());

            List<Set<Coord>> mergedBasins = new ArrayList<>();

            for (Set<Coord> basin : basins) {
                boolean joins = false;
                for (Set<Coord> mergedBasin : mergedBasins) {
                    for (Coord coord : basin) {
                        if (mergedBasin.contains(coord)) {
                            joins = true;
                            mergedBasin.addAll(basin);
                            break;
                        }
                    }
                }
                if (!joins) {
                    mergedBasins.add(basin);
                }
            }
            System.out.println("Merged Basins: " + mergedBasins.size());

            mergedBasins.sort(Comparator.comparingInt(Set::size));

            for (Set<Coord> mergedBasin : mergedBasins) {
                System.out.println(mergedBasin.size());
            }

            int resultSize = mergedBasins.size();
            int result = mergedBasins.get(resultSize - 1).size() * mergedBasins.get(resultSize - 2).size() * mergedBasins.get(resultSize - 3).size();
            System.out.println("Result " + result);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printHeights(int[][] heights) {
        for (int row = 0; row < heights.length; row++) {
            for (int col=0; col < heights[row].length; col++) {
                System.out.print(heights[row][col]);
            }
            System.out.println();
        }

    }

    static class Coord {
        final int row;
        final int col;

        Coord(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return row == coord.row &&
                    col == coord.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public String toString() {
            return "Coord{" +
                    "row=" + row +
                    ", col=" + col +
                    '}';
        }
    }


}
