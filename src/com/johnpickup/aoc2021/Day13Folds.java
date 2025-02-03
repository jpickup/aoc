package com.johnpickup.aoc2021;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day13Folds {
    public static void main(String[] args) {
        try (Stream<String> stream = Files.lines(Paths.get(
                "/Volumes/Users/john/Development/AdventOfCode/resources/Day13Input.txt"))) {

            List<String> inputs = stream.collect(Collectors.toList());

            List<Coord> coords = inputs.stream()
                    .filter(s -> s.contains(","))
                    .map(Day13Folds::parseCoord)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            List<Fold> folds = inputs.stream()
                    .filter(s -> s.contains("fold"))
                    .map(Day13Folds::parseFold)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());


            int rows = coords.stream().map(c -> c.y).reduce(0, Math::max) + 1;
            int cols = coords.stream().map(c -> c.x).reduce(0, Math::max) + 1;

            boolean[][] grid = new boolean[rows][cols];

            for (Coord coord : coords) {
                grid[coord.y][coord.x] = true;
            }


            for (Fold fold : folds) {
                System.out.printf("BEFORE Grid: %d rows * %d cols\n", grid.length, grid[0].length);
                System.out.println(" ----------- " + fold);
                grid = processFold(grid, fold);
                System.out.printf("AFTER Grid: %d rows * %d cols\n", grid.length, grid[0].length);
                //printGrid(grid);
                printCount(grid);
            }
            printGrid(grid);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean[][] processFold(boolean[][] grid, Fold fold) {
        switch(fold.axis) {
            case 'x':
                return processVerticalFold(grid, fold);
            case 'y':
                return processHorizontalFold(grid, fold);
        }
        return null;
    }

    private static boolean[][] processVerticalFold(boolean[][] grid, Fold fold) {
        int newCols = fold.value;
        int oldRows = grid[0].length;
        int newRows = grid.length;
        int mirrorCols = grid[0].length - fold.value;
        boolean[][] result = new boolean[newRows][newCols];

        System.out.println("Vert:" + fold.value);

        for (int row=0; row < newRows; row++) {
            for (int col=0; col < newCols; col++) {
                result[row][col] = grid[row][col];

                int mirrorCol = grid[0].length - col - 1;
                if (col < mirrorCols && mirrorCol > mirrorCols) {
                    result[row][col] |= grid[row][mirrorCol];
                }
            }
        }

        return result;
    }

    private static boolean[][] processHorizontalFold(boolean[][] grid, Fold fold) {
        int oldRows = grid.length;
        int newCols = grid[0].length;
        int newRows = fold.value;
        int mirrorRows = grid.length - fold.value;
        boolean[][] result = new boolean[newRows][newCols];

        System.out.println("Horz:" + fold.value);

        for (int row=0; row < newRows; row++) {
            int mirrorRow = grid.length - row + (1- oldRows%2) - 1;
            for (int col=0; col < newCols; col++) {
                result[row][col] = grid[row][col];

                if (mirrorRow < oldRows && mirrorRow > newRows) {
                    result[row][col] |= grid[mirrorRow][col];
                }
            }
        }

        return result;
    }

    private static void printGrid(boolean[][] grid) {
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                System.out.print(grid[y][x]?"#":" ");
            }
            System.out.println();
        }
    }

    private static void printCount(boolean[][] grid) {
        int count = 0;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x]) count++;
            }
        }
        System.out.println("Count: " + count);
    }


    private static Coord parseCoord(String s){
        if (s==null || s.isEmpty()) return null;

        Coord result = new Coord();

        String[] xy = s.split(",");

        result.x = Integer.parseInt(xy[0]);
        result.y = Integer.parseInt(xy[1]);

        return result;
    }

    private static Fold parseFold(String s){
        if (s==null || s.isEmpty()) return null;

        Fold result = new Fold();
        String[] parts = s.substring(11).split("=");

        result.axis=parts[0].charAt(0);
        result.value=Integer.parseInt(parts[1]);

        return result;
    }

    static class Coord {
        int x, y;
    }

    static class Fold {
        char axis;
        int value;

        @Override
        public String toString() {
            return axis + " : " + value;
        }
    }
}
